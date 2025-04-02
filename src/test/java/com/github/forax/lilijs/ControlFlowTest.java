package com.github.forax.lilijs;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class ControlFlowTest {
  private static String execute(String code) {
    var outStream = new ByteArrayOutputStream(8192);
    var printStream = new PrintStream(outStream, false, UTF_8);
    Interpreter.interpret(code, printStream);
    return outStream.toString(UTF_8).replace("\r\n", "\n");
  }

  @Test
  public void testIfStatementWithNumbers() {
    assertEquals("true branch\n", execute("""
        let x = 10;
        if (x > 5) {
            print("true branch");
        } else {
            print("false branch");
        }
        """));

    assertEquals("false branch\n", execute("""
        let x = 3;
        if (x > 5) {
            print("true branch");
        } else {
            print("false branch");
        }
        """));
  }

  @Test
  public void testIfStatementWithStrings() {
    assertEquals("strings equal\n", execute("""
        let a = "hello";
        let b = "hello";
        if (a === b) {
            print("strings equal");
        } else {
            print("strings not equal");
        }
        """));

    assertEquals("strings not equal\n", execute("""
        let a = "hello";
        let b = "world";
        if (a === b) {
            print("strings equal");
        } else {
            print("strings not equal");
        }
        """));
  }

  @Test
  public void testIfStatementWithBooleans() {
    assertEquals("true value\n", execute("""
        let flag = true;
        if (flag) {
            print("true value");
        } else {
            print("false value");
        }
        """));

    assertEquals("false value\n", execute("""
        let flag = false;
        if (flag) {
            print("true value");
        } else {
            print("false value");
        }
        """));
  }

  @Test
  public void testIfStatementWithNull() {
    assertEquals("is null\n", execute("""
        let x = null;
        if (x === null) {
            print("is null");
        } else {
            print("not null");
        }
        """));
  }

  @Test
  public void testIfStatementWithUndefined() {
    assertEquals("is undefined\n", execute("""
        let x;
        if (x === undefined) {
            print("is undefined");
        } else {
            print("not undefined");
        }
        """));
  }

  @Test
  public void testNestedIfStatementWithMixedTypes() {
    assertEquals("inner true\n", execute("""
        let x = 10;
        let y = "high";
        if (x > 5) {
            if (y === "high") {
                print("inner true");
            } else {
                print("inner false");
            }
        } else {
            print("outer false");
        }
        """));
  }

  @Test
  public void testIfElseIfStatementWithNumbers() {
    assertEquals("first branch\n", execute("""
        let x = 10;
        if (x > 5) {
            print("first branch");
        } else if (x > 3) {
            print("second branch");
        } else {
            print("third branch");
        }
        """));

    assertEquals("second branch\n", execute("""
        let x = 4;
        if (x > 5) {
            print("first branch");
        } else if (x > 3) {
            print("second branch");
        } else {
            print("third branch");
        }
        """));

    assertEquals("third branch\n", execute("""
        let x = 2;
        if (x > 5) {
            print("first branch");
        } else if (x > 3) {
            print("second branch");
        } else {
            print("third branch");
        }
        """));
  }

  @Test
  public void testIfElseIfStatementWithMixedTypes() {
    assertEquals("string branch\n", execute("""
        let x = "hello";
        if (x === "hi") {
            print("hi branch");
        } else if (x === "hello") {
            print("string branch");
        } else {
            print("default branch");
        }
        """));
  }

  @Test
  public void testWhileLoopWithNumbers() {
    assertEquals("0\n1\n2\n3\n4\n", execute("""
        let i = 0;
        while (i < 5) {
            print(i);
            i = i + 1;
        }
        """));
  }

  @Test
  public void testWhileLoopWithBoolean() {
    assertEquals("end\n", execute("""
        let v = false;
        while (v) {
            print("oops");
        }
        print("end");
        """));
  }

  @Test
  public void testDoWhileLoopWithNumbers() {
    assertEquals("5\n", execute("""
        let i = 5;
        do {
            print(i);
            i = i + 1;
        } while (i < 5);
        """));
  }

  @Test
  public void testDoWhileLoopWithBooleans() {
    assertEquals("Starting loop\nEnding loop\n", execute("""
        let flag = false;
        do {
            print("Starting loop");
            print("Ending loop");
        } while (flag);
        """));
  }

  @Test
  public void testForLoopWithNumbers() {
    assertEquals("0\n1\n2\n3\n4\n", execute("""
        for (let i = 0; i < 5; i = i + 1) {
            print(i);
        }
        """));
  }

  @Test
  public void testForLoopPostIncrementWithNumbers() {
    assertEquals("0\n1\n2\n3\n4\n", execute("""
        for (let i = 0; i < 5; i++) {
            print(i);
        }
        """));
  }

  @Test
  public void testForLoopPreIncrementWithNumbers() {
    assertEquals("0\n1\n2\n3\n4\n", execute("""
        for (let i = 0; i < 5; ++i) {
            print(i);
        }
        """));
  }

  @Test
  public void testForLoopWithALotOfNumbers() {
    assertEquals("1454\n", execute("""
        let value = 0;
        for(let i = 0; i < 1000; i++) {
          value = (value * 17 + i) % 1773;
        }
        print(value);
        """));
  }

  @Test
  public void testForLoopWithALotOfNumbers2() {
    assertEquals("1031\n", execute("""
        let value = 0;
        for(let i = 0; i < 10000; i++) {
          value = (value * 17 + i) % 1773;
        }
        print(value);
        """));
  }

  @Test
  public void testForLoopWithALotOfNumbers3() {
    assertEquals("1418\n", execute("""
        let value = 0;
        for(let i = 0; i < 100000; i++) {
          value = (value * 17 + i) % 1773;
        }
        print(value);
        """));
  }

  @Test
  public void testForLoopWithALotOfNumbers4() {
    assertEquals("257\n", execute("""
        let value = 0;
        for(let i = 0; i < 1000000; i++) {
          value = (value * 17 + i) % 1773;
        }
        print(value);
        """));
  }

  @Test
  public void testForLoopWithALotOfNumbers5() {
    assertEquals("1112\n", execute("""
        let value = 0;
        for(let i = 0; i < 10000000; i++) {
          value = (value * 17 + i) % 1773;
        }
        print(value);
        """));
  }

  @Test
  public void testForLoopNoInitWithNumbers() {
    assertEquals("0\n1\n2\n3\n4\n", execute("""
        let i = 0;
        for (; i < 5; i = i + 1) {
            print(i);
        }
        """));
  }

  @Test
  public void testForLoopNoUpdateWithNumbers() {
    assertEquals("0\n1\n2\n3\n4\n", execute("""
        for (let i = 0; i < 5;) {
            print(i);
            i = i + 1;
        }
        """));
  }

  @Test
  public void testForLoopNoTestWithNumbers() {
    assertEquals("0\n1\n2\n3\n4\n", execute("""
        for (let i = 0; ;i = i + 1) {
            if (i === 5) {
                return;
            }
            print(i);
        }
        """));
  }

  @Test
  public void testForLoopOnlyInitWithNumbers() {
    assertEquals("0\n1\n2\n3\n4\n", execute("""
        for (let i = 0; ;) {
            if (i === 5) {
               return;
            }
            print(i);
            i = i + 1;
        }
        """));
  }

  @Test
  public void testForLoopOnlyTestWithNumbers() {
    assertEquals("0\n1\n2\n3\n4\n", execute("""
        let i = 0;
        for (; i < 5;) {
            print(i);
            i = i + 1;
        }
        """));
  }

  @Test
  public void testForLoopOnlyUpdateWithNumbers() {
    assertEquals("0\n1\n2\n3\n4\n", execute("""
        let i = 0;
        for (; ; i = i + 1) {
            if (i === 5) {
                return;
            }
            print(i);}
        """));
  }

  @Test
  public void testForEverWithNumbers() {
    assertEquals("start\n", execute("""
        print("start");
        for(;;) {
          return;
        }
        print("end");
        """));
  }

  @Test @Disabled
  public void testNestedLoopsWithDifferentTypes() {
    assertEquals("0:a\n0:b\n0:c\n1:a\n1:b\n1:c\n2:a\n2:b\n2:c\n", execute("""
        let chars = "abc";
        for (let i = 0; i < 3; i = i + 1) {
            for (let j = 0; j < chars.length; j = j + 1) {
                print(i + ":" + chars.charAt(j));
            }
        }
        """));
  }

  @Test @Disabled
  public void testBreakStatementWithStringComparison() {
    assertEquals("apple\nbanana\n", execute("""
        let fruits = ["apple", "banana", "cherry", "date"];
        for (let i = 0; i < fruits.length; i = i + 1) {
            if (fruits[i] === "cherry") {
                break;
            }
            print(fruits[i]);
        }
        """));
  }

  @Test @Disabled  // FIXME
  public void testBreakStatementWithNumberComparison() {
    assertEquals("0\n1\n2\n", execute("""
        for (let i = 0; i < 10; i = i + 1) {
            if (i === 3) {
                break;
            }
            print(i);
        }
        """));
  }

  @Test @Disabled  // FIXME
  public void testContinueStatementWithStringCondition() {
    assertEquals("apple\ncherry\ndate\n", execute("""
        let fruits = ["apple", "banana", "cherry", "date"];
        for (let i = 0; i < fruits.length; i = i + 1) {
            if (fruits[i] === "banana") {
                continue;
            }
            print(fruits[i]);
        }
        """));
  }

  @Test @Disabled  // FIXME
  public void testContinueStatementWithNumberCondition() {
    assertEquals("0\n1\n2\n4\n", execute("""
        for (let i = 0; i < 5; i = i + 1) {
            if (i === 3) {
                continue;
            }
            print(i);
        }
        """));
  }

  @Test @Disabled
  public void testSwitchStatementWithNumbers() {
    assertEquals("case 1\n", execute("""
        let x = 1;
        switch (x) {
            case 0:
                print("case 0");
                break;
            case 1:
                print("case 1");
                break;
            case 2:
                print("case 2");
                break;
            default:
                print("default case");
        }
        """));
  }

  @Test @Disabled
  public void testSwitchStatementWithNumbersUsingDefault() {
    assertEquals("default case\n", execute("""
        let x = 5;
        switch (x) {
            case 0:
                print("case 0");
                break;
            case 1:
                print("case 1");
                break;
            case 2:
                print("case 2");
                break;
            default:
                print("default case");
        }
        """));
  }

  @Test @Disabled
  public void testSwitchStatementWithStrings() {
    assertEquals("Found apple\n", execute("""
        let fruit = "apple";
        switch (fruit) {
            case "banana":
                print("Found banana");
                break;
            case "apple":
                print("Found apple");
                break;
            case "cherry":
                print("Found cherry");
                break;
            default:
                print("Unknown fruit");
        }
        """));
  }

  @Test @Disabled
  public void testSwitchFallThroughWithNumbers() {
    assertEquals("case 1\ncase 2\n", execute("""
        let x = 1;
        switch (x) {
            case 0:
                print("case 0");
                break;
            case 1:
                print("case 1");
                // Intentional fall-through
            case 2:
                print("case 2");
                break;
            default:
                print("default case");
        }
        """));
  }

  @Test @Disabled
  public void testSwitchFallThroughWithStrings() {
    assertEquals("Winter\nCold season\n", execute("""
        let season = "Winter";
        switch (season) {
            case "Summer":
                print("Summer");
                print("Hot season");
                break;
            case "Winter":
                print("Winter");
                // Intentional fall-through
            case "Fall":
                print("Cold season");
                break;
            default:
                print("Unknown season");
        }
        """));
  }

  @Test @Disabled
  public void testLogicalOperatorsWithBooleans() {
    assertEquals("true\n", execute("""
        let a = true;
        let b = true;
        print(a && b);
        """));

    assertEquals("false\n", execute("""
        let a = true;
        let b = false;
        print(a && b);
        """));

    assertEquals("true\n", execute("""
        let a = true;
        let b = false;
        print(a || b);
        """));

    assertEquals("false\n", execute("""
        let a = false;
        let b = false;
        print(a || b);
        """));

    assertEquals("false\n", execute("""
        let a = true;
        print(!a);
        """));
  }

  @Test @Disabled
  public void testLogicalOperatorsWithNumbers() {
    assertEquals("5\n", execute("""
        let a = 5;
        let b = 10;
        print(a && b);  // In JS, this returns the second operand if both are truthy
        """));

    assertEquals("5\n", execute("""
        let a = 5;
        let b = 0;
        print(a || b);  // In JS, this returns the first truthy operand
        """));

    assertEquals("false\n", execute("""
        let a = 5;
        print(!a);  // In JS, this converts to boolean then negates
        """));
  }

  @Test @Disabled
  public void testLogicalOperatorsWithStrings() {
    assertEquals("world\n", execute("""
        let a = "hello";
        let b = "world";
        print(a && b);  // In JS, this returns the second operand if both are truthy
        """));

    assertEquals("hello\n", execute("""
        let a = "hello";
        let b = "";
        print(a || b);  // In JS, this returns the first truthy operand
        """));

    assertEquals("false\n", execute("""
        let a = "hello";
        print(!a);  // In JS, this converts to boolean then negates
        """));
  }

  @Test @Disabled
  public void testLogicalOperatorsWithMixedTypes() {
    assertEquals("10\n", execute("""
        let a = "hello";
        let b = 10;
        print(a && b);
        """));

    assertEquals("hello\n", execute("""
        let a = "hello";
        let b = 0;
        print(a || b);
        """));
  }

  @Test @Disabled
  public void testShortCircuitEvaluationWithBooleans() {
    assertEquals("short circuit\ntrue\n", execute("""
        function sideEffect() {
            print("short circuit");
            return true;
        }
        
        let a = false;
        let result = a && sideEffect();
        print(result);
        """));

    assertEquals("short circuit\ntrue\n", execute("""
        function sideEffect() {
            print("short circuit");
            return true;
        }
        
        let a = true;
        let result = a || sideEffect();
        print(result);
        """));
  }

  @Test @Disabled
  public void testShortCircuitEvaluationWithMixedTypes() {
    assertEquals("10\n", execute("""
        function sideEffect() {
            print("side effect");
            return 20;
        }
        
        let a = "";
        let result = a && sideEffect();
        print(result || 10);
        """));
  }

  @Test
  public void testTernaryOperatorWithBooleans() {
    assertEquals("true branch\n", execute("""
        let condition = true;
        let result = condition ? "true branch" : "false branch";
        print(result);
        """));

    assertEquals("false branch\n", execute("""
        let condition = false;
        let result = condition ? "true branch" : "false branch";
        print(result);
        """));
  }

  @Test
  public void testTernaryOperatorWithNumbers() {
    assertEquals("positive\n", execute("""
        let num = 10;
        let result = num > 0 ? "positive" : "non-positive";
        print(result);
        """));

    assertEquals("non-positive\n", execute("""
        let num = 0;
        let result = num > 0 ? "positive" : "non-positive";
        print(result);
        """));
  }

  @Test
  public void testTernaryOperatorWithStrings() {
    assertEquals("empty\n", execute("""
        let str = "";
        let result = str ? "non-empty" : "empty";
        print(result);
        """));

    assertEquals("non-empty\n", execute("""
        let str = "hello";
        let result = str ? "non-empty" : "empty";
        print(result);
        """));
  }

  @Test
  public void testTernaryOperatorWithNull() {
    assertEquals("is null\n", execute("""
        let val = null;
        let result = val ? "not null" : "is null";
        print(result);
        """));
  }

  @Test
  public void testTernaryOperatorWithUndefined() {
    assertEquals("is undefined\n", execute("""
        let val;
        let result = val ? "not undefined" : "is undefined";
        print(result);
        """));
  }

  @Test
  public void testTypeCoercionInConditions() {
    assertEquals("truthy\n", execute("""
        let val = "hello";
        if (val) {
            print("truthy");
        } else {
            print("falsy");
        }
        """));

    assertEquals("falsy\n", execute("""
        let val = "";
        if (val) {
            print("truthy");
        } else {
            print("falsy");
        }
        """));

    assertEquals("truthy\n", execute("""
        let val = 42;
        if (val) {
            print("truthy");
        } else {
            print("falsy");
        }
        """));

    assertEquals("falsy\n", execute("""
        let val = 0;
        if (val) {
            print("truthy");
        } else {
            print("falsy");
        }
        """));

    assertEquals("falsy\n", execute("""
        let val = null;
        if (val) {
            print("truthy");
        } else {
            print("falsy");
        }
        """));

    assertEquals("falsy\n", execute("""
        let val;  // undefined
        if (val) {
            print("truthy");
        } else {
            print("falsy");
        }
        """));
  }
}