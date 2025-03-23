package com.github.forax.lilijs;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AssignmentTest {
  private static String execute(String code) {
    var outStream = new ByteArrayOutputStream(8192);
    var printStream = new PrintStream(outStream, false, UTF_8);
    Interpreter.interpret(code, Interpreter.Media.JAVASCRIPT, printStream);
    return outStream.toString(UTF_8).replace("\r\n", "\n");
  }

  @Test
  public void testBasicVariableDeclaration() {
    assertEquals("10\n", execute("""
        let x = 10;
        print(x);
        """));
  }

  @Test
  public void testLetVariableDeclaration() {
    assertEquals("20\n", execute("""
        let y = 20;
        print(y);
        """));
  }

  @Test
  public void testConstVariableDeclaration() {
    assertEquals("30\n", execute("""
        const z = 30;
        print(z);
        """));
  }

  @Test
  public void testMultipleVariableDeclarations() {
    assertEquals("40\n50\n60\n", execute("""
        let a = 40, b = 50, c = 60;
        print(a);
        print(b);
        print(c);
        """));
  }

  @Test
  public void testVariableReassignment() {
    assertEquals("10\n20\n", execute("""
        let x = 10;
        print(x);
        x = 20;
        print(x);
        """));
  }

  @Test
  public void testLetReassignment() {
    assertEquals("15\n25\n", execute("""
        let y = 15;
        print(y);
        y = 25;
        print(y);
        """));
  }

  @Test @Disabled
  public void testConstReassignmentShouldFail() {
    // This should throw an exception or return an error message
    String result = execute("""
        const z = 30;
        z = 40; // This should cause an error
        print(z);
        """);
    assertTrue(result.contains("error") || result.contains("Exception"));
  }

  @Test
  public void testUndefinedVariable() {
    assertEquals("undefined\n", execute("""
        let x;
        print(x);
        """));
  }

  @Test
  public void testNullVariable() {
    assertEquals("null\n", execute("""
        let x = null;
        print(x);
        """));
  }

  @Test
  public void testVariableScopingWithBlocks() {
    assertEquals("outer\nouter\n", execute("""
        let x = "outer";
        {
            let x = "inner"; // Should overwrite the outer variable in var's case
        }
        print(x);
        
        let y = "outer";
        {
            let y = "inner"; // Should not affect the outer variable
        }
        print(y);
        """));
  }

  @Test
  public void testLetBlockScoping() {
    assertEquals("outer\ninner\nouter\n", execute("""
        let x = "outer";
        print(x);
        {
            let x = "inner";
            print(x);
        }
        print(x);
        """));
  }

  @Test
  public void testLetNoHoisting() {
    // This should throw a reference error
    assertThrows(Failure.class, () -> execute("""
        print(y); // Should cause a reference error
        let y = 20;
        """));
  }

  @Test
  public void testConstNoHoisting() {
    // This should throw a reference error
    assertThrows(Failure.class, () ->  execute("""
        print(z); // Should cause a reference error
        const z = 30;
        """));
  }

  @Test @Disabled
  public void testCompoundAssignmentOperators() {
    assertEquals("10\n15\n150\n75\n74\n", execute("""
        let x = 10;
        print(x);
        x += 5;
        print(x);
        x *= 10;
        print(x);
        x /= 2;
        print(x);
        x--;
        print(x);
        """));
  }

  @Test @Disabled
  public void testDestructuringAssignment() {
    assertEquals("10\n20\nfoo\nbar\n", execute("""
        let [a, b] = [10, 20];
        print(a);
        print(b);
        
        let {x, y} = {x: "foo", y: "bar"};
        print(x);
        print(y);
        """));
  }

  @Test @Disabled
  public void testNestedDestructuring() {
    assertEquals("10\n20\n30\n", execute("""
        let [a, [b, c]] = [10, [20, 30]];
        print(a);
        print(b);
        print(c);
        """));
  }

  @Test @Disabled
  public void testObjectDestructuringWithRename() {
    assertEquals("hello\n", execute("""
        let {x: newName} = {x: "hello"};
        print(newName);
        """));
  }

  @Test @Disabled
  public void testDestructuringWithDefaultValues() {
    assertEquals("10\ndefault\n", execute("""
        let [a, b = "default"] = [10];
        print(a);
        print(b);
        """));
  }

  @Test
  public void testComplexExpressionAssignment() {
    assertEquals("11\n", execute("""
        let x = 5;
        let y = x + 1 * 6;
        print(y);
        """));
  }

  @Test @Disabled
  public void testConditionalAssignment() {
    assertEquals("true case\n", execute("""
        let condition = true;
        let result = condition ? "true case" : "false case";
        print(result);
        """));
  }

  @Test @Disabled
  public void testLogicalAssignmentOperators() {
    assertEquals("default\nvalue\n", execute("""
        let x;
        x = x || "default";
        print(x);
        
        let y = "value";
        y = y || "default";
        print(y);
        """));
  }

  @Test @Disabled
  public void testNullishCoalescingAssignment() {
    assertEquals("default\nfalse\n0\nvalue\n", execute("""
        let a;
        a = a ?? "default";
        print(a);
        
        let b = false;
        b = b ?? "default";
        print(b);
        
        let c = 0;
        c = c ?? "default";
        print(c);
        
        let d = "value";
        d = d ?? "default";
        print(d);
        """));
  }

  @Test
  public void testChainedAssignments() {
    assertEquals("5\n5\n5\n", execute("""
        let a, b, c;
        a = b = c = 5;
        print(a);
        print(b);
        print(c);
        """));
  }

  @Test
  public void testTemporalDeadZone() {
    // This should cause a reference error
    assertThrows(Failure.class, () -> execute("""
        {
            // This is in the temporal dead zone for x
            let temp = x; // Should error
            let x = 10;
        }
        """));
  }

  @Test
  public void testRedeclarationWithLet() {
    // This should cause a syntax error
    assertThrows(Failure.class, () -> execute("""
        let y = 10;
        let y = 20; // Invalid redeclaration with let
        print(y);
        """));
  }

  @Test
  public void testFunctionScopeVsBlockScope() {
    assertEquals("outer\ninner\nouter\n", execute("""
        function test() {
            let funcScoped = "outer";
            let blockScoped = "outer";
        
            {
                let blockScoped = "inner"; // Only affects this block
                print(funcScoped);
                print(blockScoped);
            }
        
            print(funcScoped); // Should be "outer"
        }
        test();
        """));
  }

  @Test @Disabled
  public void testArrayAccessAssignment() {
    assertEquals("10\n20\n30\n", execute("""
        let arr = [10, 20, 30];
        print(arr[0]);
        print(arr[1]);
        print(arr[2]);
        
        arr[1] = 25;
        print(arr[1]); // Should now be 25
        """));
  }

  @Test @Disabled
  public void testIncrementAndDecrementOperators() {
    assertEquals("10\n11\n11\n10\n", execute("""
        let x = 10;
        print(x);
        print(++x); // Pre-increment, should be 11
        print(x--);  // Post-decrement, should still show 11
        print(x);    // Now should be 10
        """));
  }
}