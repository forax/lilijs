package com.github.forax.lilijs.main;

import com.github.forax.lilijs.Interpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("""
        lilijs file
          with file being either a TypeScript file (foo.ts)
          or a JavaScript/EcmaScript file (foo.js)
        """);
      System.exit(1);
      return;
    }
    var path = Path.of(args[0]);
    String code;
    try {
      code = Files.readString(path);
    } catch (IOException e) {
      System.err.println("an i/o error occurs " + e.getMessage());
      System.exit(2);
      return;
    }
    Interpreter.interpret(code, System.out);
  }
}
