package com.github.forax.lilijs;

import static java.util.Objects.requireNonNull;

public final class Compiler {
  public static byte[] compile(String className, String code) {
    requireNonNull(className);
    requireNonNull(code);
    var program = Parser.parse(code);
    return CodeGen.compile(className, program);
  }
}
