package com.github.forax.lilijs;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class FunctionTest {
  private static String execute(String code) {
    var outStream = new ByteArrayOutputStream(8192);
    var printStream = new PrintStream(outStream, false, UTF_8);
    Interpreter.interpret(code, Interpreter.Media.JAVASCRIPT, printStream);
    return outStream.toString(UTF_8).replace("\r\n", "\n");
  }

  @Test
  public void testBasicFunctionDeclaration() {
    assertEquals("Hello World!\n", execute("""
        function greet() {
            print("Hello World!");
        }
        greet();
        """));
  }

  @Test
  public void testFunctionWithParameters() {
    assertEquals("15\n", execute("""
        function add(a, b) {
            print(a + b);
        }
        add(7, 8);
        """));
  }

  @Test
  public void testFunctionWithReturn() {
    assertEquals("20\n", execute("""
        function multiply(a, b) {
            return a * b;
        }
        const result = multiply(4, 5);
        print(result);
        """));
  }

  @Test
  public void testFunctionExpression() {
    assertEquals("Hello from function expression!\n", execute("""
        const greet = function() {
            print("Hello from function expression!");
        };
        greet();
        """));
  }

  @Test
  public void testArrowFunction() {
    assertEquals("30\n", execute("""
        const add = (a, b) => a + b;
        print(add(10, 20));
        """));
  }

  @Test
  public void testArrowFunctionWithBlock() {
    assertEquals("Result is: 15\n", execute("""
        const calculate = (a, b) => {
            const sum = a + b;
            return "Result is: " + sum;
        };
        print(calculate(7, 8));
        """));
  }

  @Test
  public void testNestedFunctionCall() {
    assertEquals("14\n", execute("""
        function outer(a) {
            function inner(b) {
                return a + b;
            }
            return inner(10);
        }
        print(outer(4));
        """));
  }

  @Test
  public void testNestedExpressionFunctionCall() {
    assertEquals("14\n", execute("""
        function outer(a) {
            let inner = function(b) {
                return a + b;
            }
            return inner(10);
        }
        print(outer(4));
        """));
  }

  @Test
  public void testNestedArrowFunctionCall() {
    assertEquals("14\n", execute("""
        function outer(a) {
            let inner = b => a + b;
            return inner(10);
        }
        print(outer(4));
        """));
  }

  @Test
  public void testRecursiveFunction() {
    assertEquals("120\n", execute("""
        function factorial(n) {
            if (n <= 1) return 1;
            return n * factorial(n - 1);
        }
        print(factorial(5));
        """));
  }

  @Test @Disabled
  public void testFunctionWithDefaultParameters() {
    assertEquals("Hello, John!\nHello, Guest!\n", execute("""
        function greet(name = "Guest") {
            print("Hello, " + name + "!");
        }
        greet("John");
        greet();
        """));
  }

  @Test @Disabled
  public void testRestParameters() {
    assertEquals("15\n", execute("""
        function sum(...numbers) {
            let total = 0;
            for (let num of numbers) {
                total += num;
            }
            return total;
        }
        print(sum(1, 2, 3, 4, 5));
        """));
  }

  @Test @Disabled
  public void testSpreadOperatorInFunctionCall() {
    assertEquals("10\n", execute("""
        function add(a, b, c) {
            return a + b + c;
        }
        const numbers = [1, 2, 7];
        print(add(...numbers));
        """));
  }

  @Test
  public void testImmediatelyInvokedFunctionExpression() {
    assertEquals("IIFE Result: 10\n", execute("""
        const result = (function() {
            const x = 10;
            return "IIFE Result: " + x;
        })();
        print(result);
        """));
  }

  @Test
  public void testFunctionHoisting() {
    assertEquals("Function is hoisted\n", execute("""
        // Calling before declaration
        hoistedFunction();
        
        function hoistedFunction() {
            print("Function is hoisted");
        }
        """));
  }

  @Test
  public void testFunctionExpressionNoHoisting() {
    // This should throw an error since function expressions are not hoisted
    assertThrows(Failure.class, () -> execute("""
        // This should fail
        notHoisted();
        
        const notHoisted = function() {
            print("This won't work");
        };
        """));
  }

  @Test
  public void testArrowFunctionNoHoisting() {
    // This should throw an error since arrow functions are not hoisted
    assertThrows(Failure.class, () -> execute("""
        // This should fail
        arrowFn();
        
        const arrowFn = () => {
            print("This won't work");
        };
        """));
  }

  @Test @Disabled
  public void testArrowFunctionThis() {
    assertEquals("object\narrow: object\n", execute("""
        const obj = {
            type: "object",
            regularMethod: function() {
                print(this.type);
        
                // Arrow function should inherit 'this' from parent scope
                const arrowFn = () => {
                    print("arrow: " + this.type);
                };
                arrowFn();
            }
        };
        obj.regularMethod();
        """));
  }

  @Test @Disabled  // FIXME arguments is not be supported
  public void testArgumentsObjectInFunction() {
    assertEquals("3\n1,2,3\n", execute("""
        function testArguments() {
            print(arguments.length);
            print(arguments[0] + "," + arguments[1] + "," + arguments[2]);
        }
        testArguments(1, 2, 3);
        """));
  }

  @Test @Disabled  // FIXME arguments is not be supported
  public void testNoArgumentsObjectInArrowFunction() {
    // This should fail since arrow functions don't have their own arguments object
    String result = execute("""
        const arrowFn = () => {
            print(arguments.length);
        };
        arrowFn(1, 2, 3);
        """);
    assertTrue(result.contains("error") || result.contains("Exception"));
  }

  @Test
  public void testFunctionAsParameter() {
    assertEquals("Result: 15\n", execute("""
        function applyOperation(a, b, operation) {
            return operation(a, b);
        }
        
        function add(x, y) {
            return x + y;
        }
        
        print("Result: " + applyOperation(7, 8, add));
        """));
  }

  @Test
  public void testArrowFunctionAsParameter() {
    assertEquals("Result: 15\n", execute("""
        function applyOperation(a, b, operation) {
            return operation(a, b);
        }
        
        print("Result: " + applyOperation(7, 8, (x, y) => x + y));
        """));
  }

  @Test  // updating closure variable is not allowed => memory leaks
  public void testClosureCapturedVariableUpdateNotSupported() {
    assertThrows(UnsupportedOperationException.class, () -> execute("""
        function createCounter(initial) {
            let count = initial;
            return function() {
                count = count + 1;
                return count;
            };
        }
        
        createCounter(5);
        """));
  }

  @Test @Disabled
  public void testMethodCall() {
    assertEquals("Hello, John!\n", execute("""
        const person = {
            name: "John",
            greet: function() {
                print("Hello, " + this.name + "!");
            }
        };
        person.greet();
        """));
  }

  @Test @Disabled
  public void testMethodCallWithArrowFunction() {
    assertEquals("Hello, John!\n", execute("""
        const person = {
            name: "John",
            greet: function() {
                const arrowFn = () => {
                    print("Hello, " + this.name + "!");
                };
                arrowFn();
            }
        };
        person.greet();
        """));
  }

  @Test @Disabled
  public void testMethodShorthand() {
    assertEquals("Hello, John!\n", execute("""
        const person = {
            name: "John",
            greet() {
                print("Hello, " + this.name + "!");
            }
        };
        person.greet();
        """));
  }

  @Test @Disabled
  public void testFunctionBindingWithBind() {
    assertEquals("Hello, John!\n", execute("""
        const person = {
            name: "John"
        };
        
        function greet() {
            print("Hello, " + this.name + "!");
        }
        
        const boundGreet = greet.bind(person);
        boundGreet();
        """));
  }

  @Test @Disabled
  public void testFunctionBindingWithCall() {
    assertEquals("Hello, Sarah!\n", execute("""
        const person = {
            name: "Sarah"
        };
        
        function greet() {
            print("Hello, " + this.name + "!");
        }
        
        greet.call(person);
        """));
  }

  @Test @Disabled
  public void testFunctionBindingWithApply() {
    assertEquals("Sum: 15\n", execute("""
        function sum() {
            let total = 0;
            for (let i = 0; i < arguments.length; i++) {
                total += arguments[i];
            }
            print("Sum: " + total);
        }
        
        const numbers = [1, 2, 3, 4, 5];
        sum.apply(null, numbers);
        """));
  }

  @Test @Disabled  //FIXME investigate
  public void testGeneratorFunctionNotSupported() {
    assertThrows(Failure.class, () -> execute("""
        function* numberGenerator() {
            yield 1;
            yield 2;
            yield 3;
        }
        """));
  }

  @Test
  public void testGeneratorExprFunctionNotSupported() {
    assertThrows(UnsupportedOperationException.class, () -> execute("""
        let f = function* numberGenerator() {
            yield 1;
            yield 2;
            yield 3;
        };
        """));
  }

  @Test @Disabled  //FIXME investigate
  public void testAsyncFunctionNotSupported() {
    assertThrows(UnsupportedOperationException.class, () -> execute("""
        async function asyncTest() {
            return "Success";
        }
        """));
  }

  @Test
  public void testAsyncExprFunctionNotSupported() {
    assertThrows(UnsupportedOperationException.class, () -> execute("""
        let f = async function () {
            return "Success";
        };
        """));
  }

  @Test
  public void testAsyncArrowFunctionNotSupported() {
    assertThrows(UnsupportedOperationException.class, () -> execute("""
        let f = async () => "Success";
        """));
  }
}