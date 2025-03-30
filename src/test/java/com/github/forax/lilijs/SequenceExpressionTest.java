package com.github.forax.lilijs;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class SequenceExpressionTest {
  private static String execute(String code) {
    var outStream = new ByteArrayOutputStream(8192);
    var printStream = new PrintStream(outStream, false, UTF_8);
    Interpreter.interpret(code, Interpreter.Media.JAVASCRIPT, printStream);
    return outStream.toString(UTF_8).replace("\r\n", "\n");
  }

  @Test
  public void testBasicSequenceExpression() {
    assertEquals("30\n", execute("""
        let x = (10, 20, 30);
        print(x);
        """));
  }

  @Test
  public void testSequenceExpressionWithAssignments() {
    assertEquals("30\n20\n10\n", execute("""
        let a, b, c;
        (a = 10, b = 20, c = 30);
        print(c);
        print(b);
        print(a);
        """));
  }

  @Test
  public void testSequenceExpressionInCondition() {
    assertEquals("Inside if\n20\n", execute("""
        let x = 10;
        if ((x = 20, x > 15)) {
            print("Inside if");
        }
        print(x);
        """));
  }

  @Test
  public void testNestedSequenceExpressions() {
    assertEquals("30\n", execute("""
        let x = (5, (10, 20), 30);
        print(x);
        """));
  }

  @Test
  public void testSequenceWithFunctionCalls() {
    assertEquals("function called\n30\n", execute("""
        function logMessage() {
            print("function called");
            return 15;
        }
        let x = (logMessage(), 20, 30);
        print(x);
        """));
  }

  @Test
  public void testSequenceWithSideEffects() {
    assertEquals("10\n20\n30\n", execute("""
        let a = 10;
        let b = 20;
        let c = (print(a), print(b), 30);
        print(c);
        """));
  }

  @Test
  public void testSequenceExpressionInReturn() {
    assertEquals("result is 30\n", execute("""
        function test() {
            let a = 10;
            return (a = 20, a + 10);
        }
        print("result is " + test());
        """));
  }

  @Test @Disabled
  public void testSequenceExpressionWithVoidOperations() {
    assertEquals("30\n", execute("""
        let x = (void 10, void 20, 30);
        print(x);
        """));
  }
}