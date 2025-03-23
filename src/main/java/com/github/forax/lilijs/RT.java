package com.github.forax.lilijs;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;

import static java.lang.invoke.MethodHandles.dropArguments;
import static java.lang.invoke.MethodHandles.foldArguments;
import static java.lang.invoke.MethodHandles.insertArguments;
import static java.lang.invoke.MethodHandles.invoker;
import static java.lang.invoke.MethodType.methodType;

public final class RT {
  private RT() {
    throw new AssertionError();
  }

  private static final class Undefined {
    @Override
    public String toString() {
      return "undefined";
    }
  }

  static final Object UNDEFINED = new Undefined();

  private static final MethodHandle LOOKUP, LOOKUP_OR_FAIL, REGISTER, BIND_FUNCTION, INVOKE, TRUTH, LOOKUP_MH;

  static {
    var lookup = MethodHandles.lookup();
    try {
      LOOKUP = lookup.findVirtual(JSObject.class, "lookup", methodType(Object.class, String.class, Object.class));
      LOOKUP_OR_FAIL = lookup.findStatic(RT.class, "lookupOrFail", methodType(Object.class, JSObject.class, String.class));
      REGISTER = lookup.findVirtual(JSObject.class, "register", methodType(void.class, String.class, Object.class));

      BIND_FUNCTION = lookup.findStatic(RT.class, "bindFunction", methodType(Object.class, String.class, MethodHandle.class, Object[].class));

      INVOKE = lookup.findVirtual(JSFunction.class, "invoke", methodType(Object.class, Object.class, Object[].class));

      TRUTH = lookup.findStatic(RT.class, "truth", methodType(boolean.class, Object.class));

      LOOKUP_MH = lookup.findStatic(RT.class, "lookupMethodHandle", methodType(MethodHandle.class, JSObject.class, String.class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  public static Object bsm_undefined(MethodHandles.Lookup lookup, String debugName, Class<?> type) {
    return UNDEFINED;
  }

  public static Object bsm_const(MethodHandles.Lookup lookup, String debugName, Class<?> type, Object constant) {
    return constant;
  }

  public static CallSite bsm_call(MethodHandles.Lookup lookup, String debugName, MethodType type) {
    var target = INVOKE.asType(type);
    return new ConstantCallSite(target);
  }

  public static CallSite bsm_builtin(MethodHandles.Lookup lookup, String name, MethodType type, String opName) {
    return switch (name) {
      case "binary" -> new BinaryBuiltinIC(type, opName);
      case "unary" -> new UnaryBuiltinIC(type, opName);
      default -> throw new UnsupportedOperationException("unsupported " + name);
    };
  }

  private static final class BinaryBuiltinIC extends MutableCallSite {
    private static final MethodHandle SLOW_PATH, CHECK;
    static {
      var lookup = MethodHandles.lookup();
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
      var fallback = MethodHandles.foldArguments(MethodHandles.exactInvoker(type), SLOW_PATH.bindTo(this));
      setTarget(fallback);
    }

    private static boolean check(Class<?> c1, Class<?> c2, Object o1, Object o2) {
      return o1.getClass() == c1 && o2.getClass() == c2;
    }

    @SuppressWarnings("unused")  // called by a MH
    private MethodHandle slowPath(Object o1, Object o2) {
      var c1 = o1.getClass();
      var c2 = o2.getClass();
      var mh = Builtin.resolveBinary(opName, c1, c2);
      var guard = MethodHandles.guardWithTest(
          MethodHandles.insertArguments(CHECK, 0, c1, c2),
          mh,
          new BinaryBuiltinIC(type(), opName).dynamicInvoker());
      setTarget(guard);
      return mh;
    }
  }

  private static final class UnaryBuiltinIC extends MutableCallSite {
    private static final MethodHandle SLOW_PATH, CHECK;
    static {
      var lookup = MethodHandles.lookup();
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
      var fallback = MethodHandles.foldArguments(MethodHandles.exactInvoker(type), SLOW_PATH.bindTo(this));
      setTarget(fallback);
    }

    private static boolean check(Class<?> c, Object o) {
      return o.getClass() == c;
    }

    @SuppressWarnings("unused")  // called by a MH
    private MethodHandle slowPath(Object o) {
      var c = o.getClass();
      var mh = Builtin.resolveUnary(opName, c);
      var guard = MethodHandles.guardWithTest(
          MethodHandles.insertArguments(CHECK, 0, c),
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
  public static CallSite bsm_lookup(MethodHandles.Lookup lookup, String debugName, MethodType type, String variableName) {
    var classLoader = (FunctionClassLoader) lookup.lookupClass().getClassLoader();
    var globalEnv = classLoader.global();
    var target = insertArguments(LOOKUP_OR_FAIL, 0, globalEnv, variableName);
    return new ConstantCallSite(target);
  }

  private static Object bindFunction(String name, MethodHandle mh, Object... args) {
    var target = MethodHandles.insertArguments(mh, 0, args);
    return new JSFunction(name, target);
  }
  public static CallSite bsm_fndecl(MethodHandles.Lookup lookup, String debugName, MethodType type, int funId) {
    var classLoader = (FunctionClassLoader) lookup.lookupClass().getClassLoader();
    var global = classLoader.global();
    var fnInfo = classLoader.dict().decodeFnInfo(funId);
    var name = fnInfo.name();
    var parameters = fnInfo.parameters();
    var body = fnInfo.body();
    var dataMap = fnInfo.dataMap();
    var captureCount = type.parameterCount();
    if (captureCount != 0) {  // capture values ?
      // direct allocation
      var mh = CodeGen.createFunctionMH(name, parameters, body, captureCount, dataMap, global);
      var target = insertArguments(BIND_FUNCTION, 0, fnInfo.name(), mh);
      target = target.withVarargs(true).asType(type);
      return new ConstantCallSite(target);
    }
    // lazy allocation + constant
    var jsFunction = new JSFunction(name, new JSFunction.FunctionData(parameters, body, 0, dataMap, global));
    if (fnInfo.toplevel()) {  // register to global and do nothing
      global.register(jsFunction.name(), jsFunction);
      return new ConstantCallSite(MethodHandles.empty(type));
    }
    var target = MethodHandles.constant(Object.class, jsFunction);
    return new ConstantCallSite(target);
  }

  public static CallSite bsm_register(MethodHandles.Lookup lookup, String debugName, MethodType type, String functionName) {
    //throw new UnsupportedOperationException("TODO bsm_register");
    var classLoader = (FunctionClassLoader) lookup.lookupClass().getClassLoader();
    var globalEnv = classLoader.global();
    return new ConstantCallSite(insertArguments(REGISTER, 0, globalEnv, functionName));
  }

  @SuppressWarnings("unused")  // used by a method handle
  private static boolean truth(Object o) {
    return o != null && o != UNDEFINED && o != Boolean.FALSE;
  }

  public static CallSite bsm_truth(MethodHandles.Lookup lookup, String debugName, MethodType type) {
    //throw new UnsupportedOperationException("TODO bsm_truth");
    return new ConstantCallSite(TRUTH);
  }

  public static CallSite bsm_get(MethodHandles.Lookup lookup, String debugName, MethodType type, String fieldName) {
    return new ConstantCallSite(insertArguments(LOOKUP, 1, fieldName, UNDEFINED).asType(type));
  }

  public static CallSite bsm_set(MethodHandles.Lookup lookup, String debugName, MethodType type, String fieldName) {
    return new ConstantCallSite(insertArguments(REGISTER, 1, fieldName).asType(type));
  }

  @SuppressWarnings("unused")  // used by a method handle
  private static MethodHandle lookupMethodHandle(JSObject receiver, String fieldName) {
    var function = (JSFunction) receiver.lookup(fieldName, null);
    if (function == null) {
      throw new Failure("no method " + fieldName);
    }
    return function.methodHandle();
  }

  public static CallSite bsm_methodcall(MethodHandles.Lookup lookup, String name, MethodType type) {
    var combiner = insertArguments(LOOKUP_MH, 1, name).asType(methodType(MethodHandle.class, Object.class));
    var target = foldArguments(invoker(type), combiner);
    return new ConstantCallSite(target);
  }
}
