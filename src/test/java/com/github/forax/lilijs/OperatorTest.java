package com.github.forax.lilijs;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class OperatorTest {
  private static String execute(String code) {
    var outStream = new ByteArrayOutputStream(8192);
    var printStream = new PrintStream(outStream, false, UTF_8);
    Interpreter.interpret(code, Interpreter.Media.JAVASCRIPT, printStream);
    return outStream.toString(UTF_8).replace("\r\n", "\n");
  }


  @Test
  public void testAddInteger() {
    assertEquals("5\n", execute("""
        print(2 + 3);
        """));
  }

  @Test
  public void testAddDouble() {
    assertEquals("5.0\n", execute("""
        print(2.0 + 3.0);
        """));
  }

  @Test
  public void testAddMixed() {
    assertEquals("5.0\n", execute("""
        print(2.0 + 3);
        """));
    assertEquals("5.0\n", execute("""
        print(2 + 3.0);
        """));
  }

  @Test
  public void testAddString() {
    assertEquals("hello world\n", execute("""
        print("hello " + "world");
        """));
  }

  @Test
  public void testAddStringWithNumber() {
    assertEquals("value: 42\n", execute("""
        print("value: " + 42);
        """));
  }

  @Test
  public void testSubtractInteger() {
    assertEquals("3\n", execute("""
        print(5 - 2);
        """));
  }

  @Test
  public void testSubtractDouble() {
    assertEquals("3.0\n", execute("""
        print(5.0 - 2.0);
        """));
  }

  @Test
  public void testSubtractMixed() {
    assertEquals("3.0\n", execute("""
        print(5.0 - 2);
        """));
    assertEquals("3.0\n", execute("""
        print(5 - 2.0);
        """));
  }

  @Test
  public void testMultiplyInteger() {
    assertEquals("6\n", execute("""
        print(2 * 3);
        """));
  }

  @Test
  public void testMultiplyDouble() {
    assertEquals("6.0\n", execute("""
        print(2.0 * 3.0);
        """));
  }

  @Test
  public void testMultiplyMixed() {
    assertEquals("6.0\n", execute("""
        print(2.0 * 3);
        """));
    assertEquals("6.0\n", execute("""
        print(2 * 3.0);
        """));
  }

  @Test
  public void testDivideInteger() {
    assertEquals("2\n", execute("""
        print(6 / 3);
        """));
  }

  @Test
  public void testDivideIntegerToDouble() {
    assertEquals("2.5\n", execute("""
        print(5 / 2);
        """));
  }

  @Test
  public void testDivideDouble() {
    assertEquals("2.0\n", execute("""
        print(6.0 / 3.0);
        """));
  }

  @Test
  public void testDivideMixed() {
    assertEquals("2.0\n", execute("""
        print(6.0 / 3);
        """));
    assertEquals("2.0\n", execute("""
        print(6 / 3.0);
        """));
  }

  @Test
  public void testModuloInteger() {
    assertEquals("1\n", execute("""
        print(7 % 3);
        """));
  }

  @Test
  public void testModuloDouble() {
    assertEquals("1.0\n", execute("""
        print(7.0 % 3.0);
        """));
  }

  @Test
  public void testModuloMixed() {
    assertEquals("1.0\n", execute("""
        print(7.0 % 3);
        """));
    assertEquals("1.0\n", execute("""
        print(7 % 3.0);
        """));
  }

  @Test
  public void testNaN() {
    assertEquals("NaN\n", execute("""
        print(NaN);
        """));
  }

  @Test
  public void testNaNEquality() {
    assertEquals("false\n", execute("""
        print(NaN === NaN);
        """));
    assertEquals("true\n", execute("""
        print(NaN !== NaN);
        """));
  }

  @Test
  public void testNaNWithArithmeticOperations() {
    assertEquals("NaN\n", execute("""
        print(NaN + 5);
        """));
    assertEquals("NaN\n", execute("""
        print(10 - NaN);
        """));
    assertEquals("NaN\n", execute("""
        print(NaN * 2);
        """));
    assertEquals("NaN\n", execute("""
        print(10 / NaN);
        """));
  }

  @Test
  public void testNaNPropagation() {
    assertEquals("NaN\n", execute("""
        print(5 + 10 * NaN);
        """));
  }

  @Test
  public void testInfinityCreation() {
    assertEquals("Infinity\n", execute("""
        print(1 / 0);
        """));
    assertEquals("-Infinity\n", execute("""
        print(-1 / 0);
        """));
  }

  @Test
  public void testInfinityEquality() {
    assertEquals("true\n", execute("""
        print(Infinity === Infinity);
        """));
    assertEquals("true\n", execute("""
        print(-Infinity === -Infinity);
        """));
    assertEquals("false\n", execute("""
        print(Infinity === -Infinity);
        """));
    assertEquals("false\n", execute("""
        print(-Infinity === Infinity);
        """));
    assertEquals("false\n", execute("""
        print(Infinity !== Infinity);
        """));
    assertEquals("false\n", execute("""
        print(-Infinity !== -Infinity);
        """));
    assertEquals("true\n", execute("""
        print(Infinity !== -Infinity);
        """));
    assertEquals("true\n", execute("""
        print(-Infinity !== Infinity);
        """));
  }

  @Test
  @Disabled
  public void testInfinityArithmetic() {
    assertEquals("Infinity\n", execute("""
        print(Infinity + 5);
        """));
    assertEquals("Infinity\n", execute("""
        print(Infinity + Infinity);
        """));
    assertEquals("NaN\n", execute("""
        print(Infinity - Infinity);
        """));
    assertEquals("Infinity\n", execute("""
        print(Infinity * 5);
        """));
    assertEquals("Infinity\n", execute("""
        print(Infinity * Infinity);
        """));
    assertEquals("NaN\n", execute("""
        print(Infinity * 0);
        """));
    assertEquals("Infinity\n", execute("""
        print(Infinity / 5);
        """));
    assertEquals("NaN\n", execute("""
        print(Infinity / Infinity);
        """));
    assertEquals("0\n", execute("""
        print(5 / Infinity);
        """));
  }

  @Test
  public void testNegativeInfinityArithmetic() {
    assertEquals("-Infinity\n", execute("""
        print(-Infinity - 5);
        """));
    assertEquals("-Infinity\n", execute("""
        print(-Infinity * 5);
        """));
    assertEquals("Infinity\n", execute("""
        print(-Infinity * -5);
        """));
  }

  @Test
  public void testInfinityComparisons() {
    assertEquals("true\n", execute("""
        print(Infinity > 1000000);
        """));
    assertEquals("true\n", execute("""
        print(-Infinity < -1000000);
        """));
    assertEquals("true\n", execute("""
        print(Infinity >= 1000000);
        """));
    assertEquals("true\n", execute("""
        print(-Infinity <= -1000000);
        """));
    assertEquals("false\n", execute("""
        print(Infinity < Infinity);
        """));
    assertEquals("false\n", execute("""
        print(Infinity > Infinity);
        """));
    assertEquals("true\n", execute("""
        print(Infinity >= Infinity);
        """));
    assertEquals("true\n", execute("""
        print(Infinity <= Infinity);
        """));
  }

  @Test
  public void testNaNComparisons() {
    assertEquals("false\n", execute("""
        print(NaN > 0);
        """));
    assertEquals("false\n", execute("""
        print(NaN < 0);
        """));
    assertEquals("false\n", execute("""
        print(NaN >= 0);
        """));
    assertEquals("false\n", execute("""
        print(NaN <= 0);
        """));
    assertEquals("false\n", execute("""
        print(NaN > NaN);
        """));
    assertEquals("false\n", execute("""
        print(NaN >= NaN);
        """));
    assertEquals("false\n", execute("""
        print(NaN < NaN);
        """));
    assertEquals("false\n", execute("""
        print(NaN <= NaN);
        """));
  }

  @Test
  public void testDivisionByZero() {
    assertEquals("Infinity\n", execute("""
        print(5 / 0);
        """));
    assertEquals("-Infinity\n", execute("""
        print(-5 / 0);
        """));
  }

  @Test
  public void testZeroDividedByZero() {
    assertEquals("NaN\n", execute("""
        print(0 / 0);
        """));
  }

  @Test
  public void testInfinityDividedByInfinity() {
    assertEquals("NaN\n", execute("""
        print(Infinity / Infinity);
        """));
  }

  @Test
  public void testStringWithSpecialValues() {
    assertEquals("NaN\n", execute("""
        print(NaN);
        """));
    assertEquals("Infinity\n", execute("""
        print(Infinity);
        """));
    assertEquals("-Infinity\n", execute("""
        print(-Infinity);
        """));
  }


  @Test
  public void testLessThanInteger() {
    assertEquals("true\n", execute("""
        print(2 < 3);
        """));
    assertEquals("false\n", execute("""
        print(3 < 3);
        """));
  }

  @Test
  public void testLessThanDouble() {
    assertEquals("true\n", execute("""
        print(2.0 < 3.0);
        """));
    assertEquals("false\n", execute("""
        print(3.0 < 3.0);
        """));
  }

  @Test
  public void testLessThanString() {
    assertEquals("true\n", execute("""
        print("2" < "3");
        """));
    assertEquals("false\n", execute("""
        print("3" < "3");
        """));
  }

  @Test
  public void testLessThanOrEqualInteger() {
    assertEquals("true\n", execute("""
        print(2 <= 3);
        """));
    assertEquals("true\n", execute("""
        print(3 <= 3);
        """));
    assertEquals("false\n", execute("""
        print(4 <= 3);
        """));
  }

  @Test
  public void testLessThanOrEqualDouble() {
    assertEquals("true\n", execute("""
        print(2.0 <= 3.0);
        """));
    assertEquals("true\n", execute("""
        print(3.0 <= 3.0);
        """));
    assertEquals("false\n", execute("""
        print(4.0 <= 3.0);
        """));
  }

  @Test
  public void testLessThanOrEqualString() {
    assertEquals("true\n", execute("""
        print("2" <= "3");
        """));
    assertEquals("true\n", execute("""
        print("3" <= "3");
        """));
    assertEquals("false\n", execute("""
        print("4" <= "3");
        """));
  }

  @Test
  public void testGreaterThanInteger() {
    assertEquals("true\n", execute("""
        print(3 > 2);
        """));
    assertEquals("false\n", execute("""
        print(3 > 3);
        """));
  }

  @Test
  public void testGreaterThanDouble() {
    assertEquals("true\n", execute("""
        print(3.0 > 2.0);
        """));
    assertEquals("false\n", execute("""
        print(3.0 > 3.0);
        """));
  }

  @Test
  public void testGreaterThanString() {
    assertEquals("true\n", execute("""
        print("3" > "2");
        """));
    assertEquals("false\n", execute("""
        print("3" > "3");
        """));
  }

  @Test
  public void testGreaterThanOrEqualInteger() {
    assertEquals("true\n", execute("""
        print(3 >= 2);
        """));
    assertEquals("true\n", execute("""
        print(3 >= 3);
        """));
    assertEquals("false\n", execute("""
        print(2 >= 3);
        """));
  }

  @Test
  public void testGreaterThanOrEqualDouble() {
    assertEquals("true\n", execute("""
        print(3.0 >= 2.0);
        """));
    assertEquals("true\n", execute("""
        print(3.0 >= 3.0);
        """));
    assertEquals("false\n", execute("""
        print(2.0 >= 3.0);
        """));
  }

  @Test
  public void testGreaterThanOrEqualString() {
    assertEquals("true\n", execute("""
        print("3" >= "2");
        """));
    assertEquals("true\n", execute("""
        print("3" >= "3");
        """));
    assertEquals("false\n", execute("""
        print("2" >= "3");
        """));
  }

  @Test
  public void testEqualityInteger() {
    assertEquals("true\n", execute("""
        print(3 === 3);
        """));
    assertEquals("false\n", execute("""
        print(3 === 4);
        """));
  }

  @Test
  public void testEqualityBoolean() {
    assertEquals("true\n", execute("""
        print(true === true);
        """));
    assertEquals("false\n", execute("""
        print(true === false);
        """));
  }

  @Test
  public void testEqualityDouble() {
    assertEquals("true\n", execute("""
        print(3.0 === 3.0);
        """));
    assertEquals("false\n", execute("""
        print(3.0 === 4.0);
        """));
  }

  @Test
  public void testEqualityString() {
    assertEquals("true\n", execute("""
        print("3" === "3");
        """));
    assertEquals("false\n", execute("""
        print("3" === "4");
        """));
  }

  @Test
  public void testInequalityInteger() {
    assertEquals("true\n", execute("""
        print(3 !== 4);
        """));
    assertEquals("false\n", execute("""
        print(3 !== 3);
        """));
  }

  @Test
  public void testInequalityBoolean() {
    assertEquals("true\n", execute("""
        print(true !== false);
        """));
    assertEquals("false\n", execute("""
        print(true !== true);
        """));
  }

  @Test
  public void testInequalityDouble() {
    assertEquals("true\n", execute("""
        print(3.0 !== 4.0);
        """));
    assertEquals("false\n", execute("""
        print(3.0 !== 3.0);
        """));
  }

  @Test
  public void testInequalityString() {
    assertEquals("true\n", execute("""
        print("3" !== "4");
        """));
    assertEquals("false\n", execute("""
        print("3" !== "3");
        """));
  }

    /*
    @Test
    public void testSpecialValuesTypeOf() {
      assertEquals("number\n", execute("""
            print(typeof NaN);
            """));
      assertEquals("number\n", execute("""
            print(typeof Infinity);
            """));
      assertEquals("number\n", execute("""
            print(typeof -Infinity);
            """));
    }

    @Test
    public void testLogicalAnd() {
      assertEquals("true\n", execute("""
          print(true && true);
          """));
      assertEquals("false\n", execute("""
          print(true && false);
          """));
      assertEquals("false\n", execute("""
          print(false && true);
          """));
      assertEquals("false\n", execute("""
          print(false && false);
          """));
    }

    @Test
    public void testLogicalOr() {
      assertEquals("true\n", execute("""
          print(true || true);
          """));
      assertEquals("true\n", execute("""
          print(true || false);
          """));
      assertEquals("true\n", execute("""
          print(false || true);
          """));
      assertEquals("false\n", execute("""
          print(false || false);
          """));
    }

    @Test
    public void testBitwiseAnd() {
      assertEquals("0\n", execute("""
          print(5 & 2);
          """));
      assertEquals("2\n", execute("""
          print(6 & 3);
          """));
    }

    @Test
    public void testBitwiseOr() {
      assertEquals("7\n", execute("""
          print(5 | 2);
          """));
      assertEquals("7\n", execute("""
          print(6 | 3);
          """));
    }

    @Test
    public void testBitwiseXor() {
      assertEquals("7\n", execute("""
          print(5 ^ 2);
          """));
      assertEquals("5\n", execute("""
          print(6 ^ 3);
          """));
    }

    @Test
    public void testLeftShift() {
      assertEquals("20\n", execute("""
          print(5 << 2);
          """));
    }

    @Test
    public void testRightShift() {
      assertEquals("1\n", execute("""
          print(5 >> 2);
          """));
    }

    @Test
    public void testUnsignedRightShift() {
      assertEquals("1\n", execute("""
          print(5 >>> 2);
          """));
      // Testing with negative number
      assertEquals("1073741823\n", execute("""
          print(-2 >>> 2);
          """));
    }

    @Test
    public void testExponential() {
      assertEquals("8\n", execute("""
          print(2 ** 3);
          """));
      assertEquals("9.0\n", execute("""
          print(3.0 ** 2);
          """));
    }

    @Test
    public void testNullishCoalescing() {
      assertEquals("default\n", execute("""
          print(null ?? "default");
          """));
      assertEquals("default\n", execute("""
          print(undefined ?? "default");
          """));
      assertEquals("value\n", execute("""
          print("value" ?? "default");
          """));
      assertEquals("0\n", execute("""
          print(0 ?? "default");
          """));
    }

    @Test
    public void testOptionalChaining() {
      assertEquals("undefined\n", execute("""
          let obj = null;
          print(obj?.property);
          """));
      assertEquals("value\n", execute("""
          let obj = {property: "value"};
          print(obj?.property);
          """));
    }

    @Test
    public void testInOperator() {
      assertEquals("true\n", execute("""
          let obj = {prop: "value"};
          print("prop" in obj);
          """));
      assertEquals("false\n", execute("""
          let obj = {prop: "value"};
          print("missing" in obj);
          """));
    }

    @Test
    public void testInstanceofOperator() {
      assertEquals("true\n", execute("""
          print([] instanceof Array);
          """));
      assertEquals("false\n", execute("""
          print("string" instanceof Array);
          """));
    }

    @Test
    public void testCommaOperator() {
      assertEquals("3\n", execute("""
          print((1, 2, 3));
          """));
    }*/
}