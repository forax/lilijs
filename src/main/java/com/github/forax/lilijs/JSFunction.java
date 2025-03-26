package com.github.forax.lilijs;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import static java.util.Objects.requireNonNull;

public final class JSFunction {
  private final String name;
  private Object data;  // FunctionData | MethodHandle

  private static final MethodHandle INVOKER;
  static {
    try {
      INVOKER = MethodHandles.lookup().findVirtual(Invoker.class, "invoke", MethodType.methodType(Object.class, Object.class, Object[].class));
    } catch (NoSuchMethodException | IllegalAccessException e) {
      throw new AssertionError(e);
    }
  }

  public interface MethodHandleProvider {
    MethodHandle provide();
  }

  public interface Invoker {
    Object invoke(Object receiver, Object... args);
  }

  private static MethodHandle asMethodHandle(Invoker invoker) {
    return INVOKER.bindTo(invoker).withVarargs(true);
  }

  public static final MethodHandle NO_INVOKER_MH =
      asMethodHandle((_, _) -> { throw new Failure("can not be invoked"); });

  private JSFunction(String name, Object data) {
    this.name = requireNonNull(name);
    this.data = requireNonNull(data);
  }

  public JSFunction(String name, MethodHandleProvider mhProvider) {
    this(name, (Object) mhProvider);
  }

  public JSFunction(String name, MethodHandle mh) {
    this(name, (Object) mh);
  }

  public JSFunction(String name, Invoker invoker) {
    this(name, asMethodHandle(requireNonNull(invoker)));
  }
  
  public String name() {
		return name;
	}
  public MethodHandle methodHandle() {
    if (data instanceof MethodHandle mh) {
      return mh;
    }
    return lazyCreateMH();
  }

  private MethodHandle lazyCreateMH() {
    var mhProvider = (MethodHandleProvider) data;
    var mh = mhProvider.provide();
    data = mh;
    return mh;
  }

  public Object invoke(Object receiver, Object... args) {
    //System.err.println("invoke " + this + " " + receiver + " " + java.util.Arrays.toString(args));
    var mh = data instanceof MethodHandle dataMH ? dataMH : lazyCreateMH();

    //System.err.println("invoke mh " + mh);

    if (!mh.isVarargsCollector() && args.length != mh.type().parameterCount() - 1) {
      throw new Failure("arguments doesn't match parameters count " + args.length + " " + (mh.type().parameterCount() - 1));
    }
    var array = new Object[args.length + 1];
    array[0] = receiver;
    System.arraycopy(args, 0, array, 1, args.length);
    try {
      return mh.invokeWithArguments(array);
    } catch(RuntimeException | Error e) {
      throw e;
    } catch (Throwable e) {
      throw new Failure(e.getMessage(), e);
    }
  }
}