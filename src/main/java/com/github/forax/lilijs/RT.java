package com.github.forax.lilijs;

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClass;
import com.github.forax.lilijs.JSFunction.MethodHandleProvider;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;
import java.lang.invoke.TypeDescriptor;

import static java.lang.invoke.MethodHandles.constant;
import static java.lang.invoke.MethodHandles.dropArguments;
import static java.lang.invoke.MethodHandles.exactInvoker;
import static java.lang.invoke.MethodHandles.foldArguments;
import static java.lang.invoke.MethodHandles.guardWithTest;
import static java.lang.invoke.MethodHandles.insertArguments;
import static java.lang.invoke.MethodHandles.invoker;
import static java.lang.invoke.MethodHandles.lookup;
import static java.lang.invoke.MethodType.methodType;

public final class RT {
  private RT() {
    throw new AssertionError();
  }

  public static final class UndefinedType {
    @Override
    public String toString() {
      return "undefined";
    }
  }

  static final Object UNDEFINED = new UndefinedType();

  private static final ClassValue<JSObject> PROTOTYPES = new ClassValue<>() {
    @Override
    protected JSObject computeValue(Class<?> type) {
      return new JSObject(null);
    }
  };

  static JSObject prototype(Class<?> jsClass) {
    return PROTOTYPES.get(jsClass);
  }

  private static final MethodHandle LOOKUP_OR_FAIL, BIND_FUNCTION;
  static {
    var lookup = lookup();
    try {
      LOOKUP_OR_FAIL = lookup.findStatic(RT.class, "lookupOrFail", methodType(Object.class, JSObject.class, String.class));
      //REGISTER = lookup.findVirtual(JSObject.class, "register", methodType(void.class, String.class, Object.class));

      BIND_FUNCTION = lookup.findStatic(RT.class, "bindFunction", methodType(Object.class, String.class, MethodHandle.class, Object[].class));
     } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  public static Object bsm_undefined(Lookup lookup, String debugName, Class<?> type) {
    return UNDEFINED;
  }

  public static Object bsm_bool(Lookup lookup, String debugName, Class<?> type, Object constant) {
    return (int) constant == 1;
  }

  public static Object bsm_const(Lookup lookup, String debugName, Class<?> type, Object constant) {
    return constant;
  }

  public static CallSite bsm_call(Lookup lookup, String debugName, MethodType type) {
    return new CallIC(type);
  }

  // speculate that the callee is always the same
  // revert to use JSFunction.invoke otherwise
  private static final class CallIC extends MutableCallSite {
    private static final MethodHandle SLOW_PATH, FALLBACK, INVOKE, CHECK;
    static {
      var lookup = lookup();
      try {
        SLOW_PATH = lookup.findVirtual(CallIC.class, "slowPath", methodType(MethodHandle.class, Object.class));
        CHECK = lookup.findStatic(CallIC.class, "check", methodType(boolean.class, Object.class, Object.class));
        FALLBACK = lookup.findVirtual(CallIC.class, "fallback", methodType(MethodHandle.class));
        INVOKE = lookup.findStatic(CallIC.class, "invoke", methodType(Object.class, Object.class, Object.class, Object[].class));
      } catch (NoSuchMethodException | IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }

    public CallIC(MethodType type) {
      super(type);
      var fallback = foldArguments(exactInvoker(type), SLOW_PATH.bindTo(this));
      setTarget(fallback);
    }

    private static JSFunction checkFunction(Object callee) {
      if (!(callee instanceof JSFunction function)) {
        throw new Failure("not a JSFunction ! " + callee);
      }
      return function;
    }

    private static boolean check(Object expected, Object o) {
      return expected == o;
    }

    private MethodHandle slowPath(Object callee) {
      var function = checkFunction(callee);
      var mh = function.methodHandle().asType(type().dropParameterTypes(0, 1));
      var target = dropArguments(mh, 0, Object.class);
      var guard = guardWithTest(
          CHECK.bindTo(function),
          target.asType(type()),
          foldArguments(exactInvoker(type()), FALLBACK.bindTo(this)));
      setTarget(guard);
      return target;
    }

    private static Object invoke(Object callee, Object receiver, Object[] args) {
      var function = checkFunction(callee);
      return function.invoke(receiver, args);
    }

    private MethodHandle fallback() {
      // revert to call JSFunction.invoke(...)
      var target = INVOKE.asType(type());
      setTarget(target);
      return target;
    }
  }

  public static CallSite bsm_builtin(Lookup lookup, String name, MethodType type, String opName) {
    return switch (name) {
      case "binary" -> new BinaryBuiltinIC(type, opName);
      case "unary" -> new UnaryBuiltinIC(type, opName);
      default -> throw new UnsupportedOperationException("unsupported " + name);
    };
  }

  private static final class BinaryBuiltinIC extends MutableCallSite {
    private static final MethodHandle SLOW_PATH, CHECK;
    static {
      var lookup = lookup();
      try {
        SLOW_PATH = lookup.findVirtual(BinaryBuiltinIC.class, "slowPath", methodType(MethodHandle.class, Object.class, Object.class));
        CHECK = lookup.findStatic(BinaryBuiltinIC.class, "check", methodType(boolean.class, Class.class, Class.class, Object.class, Object.class));
      } catch (NoSuchMethodException | IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }

    private final String opName;

    public BinaryBuiltinIC(MethodType type, String opName) {
      super(type);
      this.opName = opName;
      var fallback = foldArguments(exactInvoker(type), SLOW_PATH.bindTo(this));
      setTarget(fallback);
    }

    private static boolean check(Class<?> c1, Class<?> c2, Object o1, Object o2) {
      return o1 != null && o1.getClass() == c1 && o2 != null && o2.getClass() == c2;
    }

    private MethodHandle slowPath(Object o1, Object o2) {
      var c1 = o1 == null ? Object.class : o1.getClass();
      var c2 = o2 == null ? Object.class : o2.getClass();
      var stub = Builtin.resolveBinary(opName, c1, c2);
      var mh = stub.mh();
      if (stub.generic()) {
        setTarget(mh);
        return mh;
      }
      var guard = guardWithTest(
          insertArguments(CHECK, 0, c1, c2),
          mh,
          new BinaryBuiltinIC(type(), opName).dynamicInvoker());
      setTarget(guard);
      return mh;
    }
  }

  private static final class UnaryBuiltinIC extends MutableCallSite {
    private static final MethodHandle SLOW_PATH, CHECK;
    static {
      var lookup = lookup();
      try {
        SLOW_PATH = lookup.findVirtual(UnaryBuiltinIC.class, "slowPath", methodType(MethodHandle.class, Object.class));
        CHECK = lookup.findStatic(UnaryBuiltinIC.class, "check", methodType(boolean.class, Class.class, Object.class));
      } catch (NoSuchMethodException | IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }

    private final String opName;

    public UnaryBuiltinIC(MethodType type, String opName) {
      super(type);
      this.opName = opName;
      var fallback = foldArguments(exactInvoker(type), SLOW_PATH.bindTo(this));
      setTarget(fallback);
    }

    private static boolean check(Class<?> c, Object o) {
      return o!= null && o.getClass() == c;
    }

    private MethodHandle slowPath(Object o) {
      var c = o == null ? Object.class : o.getClass();
      var stub = Builtin.resolveUnary(opName, c);
      var mh = stub.mh();
      if (stub.generic()) {
        setTarget(mh);
        return mh;
      }
      var guard = guardWithTest(
          insertArguments(CHECK, 0, c),
          mh,
          new UnaryBuiltinIC(type(), opName).dynamicInvoker());
      setTarget(guard);
      return mh;
    }
  }

  private static Object lookupOrFail(JSObject jsObject, String key) {
    var value = jsObject.lookup(key, null);
    if (value == null) {
      throw new Failure("no value for " + key);
    }
    return value;
  }
  public static CallSite bsm_lookup(Lookup lookup, String debugName, MethodType type, String variableName) {
    var classLoader = (IsolateClassLoader) lookup.lookupClass().getClassLoader();
    var globalEnv = classLoader.global();
    var target = insertArguments(LOOKUP_OR_FAIL, 0, globalEnv, variableName);
    return new ConstantCallSite(target);
  }

  private static Object bindFunction(String name, MethodHandle mh, Object... args) {
    var target = insertArguments(mh, 0, args);
    return new JSFunction(name, target);
  }
  public static Object bsm_fndecl(Lookup lookup, String debugName, TypeDescriptor typeDescriptor, int funId) {
    var classLoader = (IsolateClassLoader) lookup.lookupClass().getClassLoader();
    var global = classLoader.global();
    var info = classLoader.dict().decodeInfo(funId);
    var name = info.name();
    var toplevel = info.toplevel();
    var parameters = info.parameters();
    var body = info.body();
    var dataMap = info.dataMap();
    var captureCount = typeDescriptor instanceof MethodType type ? type.parameterCount() : 0;
    MethodHandleProvider provider = body instanceof Swc4jAstClass astClass ?
      () -> CodeGen.createClassFunctionMH(name, astClass, captureCount, dataMap, global) :
      () -> CodeGen.createFunctionMH(name, parameters, body, captureCount, dataMap, global);

    // Is it an invokedynamic with captured arguments ?
    if (typeDescriptor instanceof MethodType type) {
      // direct allocation
      var mh = provider.provide();
      var target = insertArguments(BIND_FUNCTION, 0, name, mh);
      target = target.withVarargs(true).asType(type);
      return new ConstantCallSite(target);
    }

    // it's a constant dynamic => lazy allocation
    var jsFunction = new JSFunction(name, provider);
    if (toplevel) {  // register to global
      global.register(jsFunction.name(), jsFunction);
    }
    return jsFunction;
  }

  /*public static CallSite bsm_register(Lookup lookup, String debugName, MethodType type, String functionName) {
    var classLoader = (IsolateClassLoader) lookup.lookupClass().getClassLoader();
    var globalEnv = classLoader.global();
    return new ConstantCallSite(insertArguments(REGISTER, 0, globalEnv, functionName));
  }*/

  public static CallSite bsm_truth(Lookup lookup, String debugName, MethodType type) {
    return new TruthBuiltinIC(type);
  }

  // try to do a class check so the result (true or false) is a constant
  // if the value is null or undefined, those should be checked *before* the class check
  // if there are more than one class, revert and call the generic `Builtin.truth(o)`
  private static final class TruthBuiltinIC extends MutableCallSite {
    private static final MethodHandle SLOW_PATH, FALLBACK, CHECK, CLASS_CHECK;
    static {
      var lookup = lookup();
      try {
        SLOW_PATH = lookup.findVirtual(TruthBuiltinIC.class, "slowPath", methodType(boolean.class, Object.class));
        FALLBACK = lookup.findVirtual(TruthBuiltinIC.class, "fallback", methodType(boolean.class, Object.class));
        CHECK = lookup.findStatic(TruthBuiltinIC.class, "check", methodType(boolean.class, Object.class, Object.class));
        CLASS_CHECK = lookup.findStatic(TruthBuiltinIC.class, "classCheck", methodType(boolean.class, Class.class, Object.class));
      } catch (NoSuchMethodException | IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }
    public TruthBuiltinIC(MethodType type) {
      super(type);
      var fallback = SLOW_PATH.bindTo(this);
      setTarget(fallback);
    }

    private static boolean check(Object expected, Object o) {
      return expected == o;
    }

    private static boolean classCheck(Class<?> c, Object o) {
      return o!= null && o.getClass() == c;
    }

    private boolean slowPath(Object o) {
      // add null check and undefined check, then the type check
      if (o == null || o == RT.UNDEFINED) {
        var guard = guardWithTest(CHECK.bindTo(o),
            dropArguments(constant(boolean.class, false), 0, Object.class),
            new TruthBuiltinIC(type()).dynamicInvoker());
        setTarget(guard);
        return false;
      }
      var result = Builtin.truth(o);
      var type = o.getClass();
      var stub = Builtin.resolveTruth(type);
      var mh = stub.mh();
      // even if the stub is generic, we still want the VM to know the type of 'o'
      var guard = guardWithTest(CLASS_CHECK.bindTo(type),
          mh,
          FALLBACK.bindTo(this));
      setTarget(guard);
      return result;
    }

    private boolean fallback(Object o) {
      // add null check and undefined check in front
      if (o == null || o == RT.UNDEFINED) {
        var guard = guardWithTest(CHECK.bindTo(o),
            dropArguments(constant(boolean.class, false), 0, Object.class),
            getTarget());
        setTarget(guard);
        return false;
      }
      // more than one type, revert to default strategy
      setTarget(Builtin.resolveTruth(Object.class).mh());
      return Builtin.truth(o);
    }
  }

  public static CallSite bsm_get(Lookup lookup, String debugName, MethodType type, String fieldName) {
    return new FieldGetIC(type, lookup, fieldName);
  }
  private static final class FieldGetIC extends MutableCallSite {
    private static final MethodHandle SLOW_PATH, CHECK;
    static {
      var lookup = lookup();
      try {
        SLOW_PATH = lookup.findVirtual(FieldGetIC.class, "slowPath", methodType(MethodHandle.class, Object.class));
        CHECK = lookup.findStatic(FieldGetIC.class, "check", methodType(boolean.class, Class.class, Object.class));
      } catch (NoSuchMethodException | IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }

    private final Lookup lookup;
    private final String fieldName;

    public FieldGetIC(MethodType type, Lookup lookup, String fieldName) {
      super(type);
      this.lookup = lookup;
      this.fieldName = fieldName;
      var fallback = foldArguments(exactInvoker(type), SLOW_PATH.bindTo(this));
      setTarget(fallback);
    }

    private static boolean check(Class<?> c, Object o) {
      return o!= null && o.getClass() == c;
    }

    private MethodHandle slowPath(Object o) throws IllegalAccessException {
      if (o == null) {
        throw new Failure("reference is null");
      }
      var receiver = o.getClass();
      MethodHandle mh;
      try {
        mh = lookup.findGetter(receiver, fieldName, Object.class);
      } catch (NoSuchFieldException e) {
        throw new Failure("no field '" + fieldName + "' found in " + receiver.getSimpleName(), e);
      }
      var target = mh.asType(type());
      var guard = guardWithTest(
          insertArguments(CHECK, 0, receiver),
          target,
          new FieldGetIC(type(), lookup, fieldName).dynamicInvoker());
      setTarget(guard);
      return target;
    }
  }


  public static CallSite bsm_set(Lookup lookup, String debugName, MethodType type, String fieldName) {
    return new FieldSetIC(type, lookup, fieldName);
  }
  private static final class FieldSetIC extends MutableCallSite {
    private static final MethodHandle SLOW_PATH, CHECK;
    static {
      var lookup = lookup();
      try {
        SLOW_PATH = lookup.findVirtual(FieldSetIC.class, "slowPath", methodType(MethodHandle.class, Object.class));
        CHECK = lookup.findStatic(FieldSetIC.class, "check", methodType(boolean.class, Class.class, Object.class));
      } catch (NoSuchMethodException | IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }

    private final Lookup lookup;
    private final String fieldName;

    public FieldSetIC(MethodType type, Lookup lookup, String fieldName) {
      super(type);
      this.lookup = lookup;
      this.fieldName = fieldName;
      var fallback = foldArguments(exactInvoker(type), SLOW_PATH.bindTo(this));
      setTarget(fallback);
    }

    private static boolean check(Class<?> c, Object o) {
      return o!= null && o.getClass() == c;
    }

    private MethodHandle slowPath(Object o) throws IllegalAccessException {
      if (o == null) {
        throw new Failure("reference is null");
      }
      var receiver = o.getClass();
      MethodHandle mh;
      try {
        mh = lookup.findSetter(receiver, fieldName, Object.class);
      } catch (NoSuchFieldException e) {
        throw new Failure("no field '" + fieldName + "' found in " + receiver.getSimpleName(), e);
      }
      var target = mh.asType(type());
      var guard = guardWithTest(
          insertArguments(CHECK, 0, receiver),
          target,
          new FieldGetIC(type(), lookup, fieldName).dynamicInvoker());
      setTarget(guard);
      return target;
    }
  }

  public static CallSite bsm_methodcall(Lookup lookup, String debugName, MethodType type, String name) {
    return new MethodCallIC(type, lookup, name);
  }
  private static final class MethodCallIC extends MutableCallSite {
    private static final MethodHandle SLOW_PATH, CHECK;
    static {
      var lookup = lookup();
      try {
        SLOW_PATH = lookup.findVirtual(MethodCallIC.class, "slowPath", methodType(MethodHandle.class, Object.class));
        CHECK = lookup.findStatic(MethodCallIC.class, "check", methodType(boolean.class, Class.class, Object.class));
      } catch (NoSuchMethodException | IllegalAccessException e) {
        throw new AssertionError(e);
      }
    }

    private final Lookup lookup;
    private final String name;

    public MethodCallIC(MethodType type, Lookup lookup, String name) {
      super(type);
      this.lookup = lookup;
      this.name = name;
      var fallback = foldArguments(exactInvoker(type), SLOW_PATH.bindTo(this));
      setTarget(fallback);
    }

    private static boolean check(Class<?> c, Object o) {
      return o!= null && o.getClass() == c;
    }

    private MethodHandle slowPath(Object o) throws IllegalAccessException {
      if (o == null) {
        throw new Failure("reference is null");
      }
      var receiver = o.getClass();
      MethodHandle getter;
      try {
        getter = lookup.findGetter(receiver, name, Object.class);
      } catch (NoSuchFieldException e) {
        getter = null;
      }
      MethodHandle target;
      if (getter != null) {  // field access
        var call = new CallIC(type().insertParameterTypes(0, Object.class)).dynamicInvoker();
        target = MethodHandles.foldArguments(call, getter.asType(methodType(Object.class, Object.class)));
      } else {  // direct method call
        var prototype = prototype(receiver);
        var maybeFunction = prototype.lookup(name, null);
        if (!(maybeFunction instanceof JSFunction function)) {
          throw new Failure("no method named " + name + " in " + receiver.getSimpleName());
        }
        var mh = function.methodHandle();
        target = mh.asType(type());
      }
      var guard = guardWithTest(
          insertArguments(CHECK, 0, receiver),
          target,
          new MethodCallIC(type(), lookup, name).dynamicInvoker());
      setTarget(guard);
      return target;
    }
  }
}
