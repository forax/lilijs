package com.github.forax.lilijs;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class CompilerTest {
  @Test
  public void compileFunction() {
    Compiler.compile("Main", """
        function hello() {
          print("hello");
        }
        """);
  }

  @Test @Disabled
  public void compileClass() {
    Compiler.compile("Main", """
        class Person {
          name : string;
          age : number;

          constructor(name : string, age : number) {
            this.name = name;
            this.age = age;
          }
        }
        """);
  }
}
