package com.github.forax.lilijs;

final class FunctionClassLoader extends ClassLoader {
  private final Dict dict;
  private final JSObject global; 
  
  FunctionClassLoader(Dict dict, JSObject global) {
    this.dict = dict;
    this.global = global;
  }

  public JSObject global() {
    return global;
  }

  public Dict dict() {
    return dict;
  }
  
  public Class<?> createClass(String name, byte[] instrs) {
    return defineClass(name, instrs, 0, instrs.length);
  }
}