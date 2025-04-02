package com.github.forax.lilijs;

import java.util.List;

public final class Linter {
  public static void lint(String code) {
    var program = Parser.parse(code);
    CodeGen.analyzeFunction(false, List.of(), program);
  }
}
