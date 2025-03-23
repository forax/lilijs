package com.github.forax.lilijs;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;

class Builtin {
  private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
  private static final HashMap<String, MethodHandle> BUILTIN_MAP = new HashMap<>();
  private static final HashMap<String, MethodHandle> VARIANT_MAP = new HashMap<>();

  private static MethodHandle resolveBinaryBuiltin(String builtin, Class<?> returnType, Class<?> type1, Class<?> type2) {
    var mangled = type1.getName() + "/" + builtin + "/" + type2.getName();
    return BUILTIN_MAP.computeIfAbsent(mangled, _ -> {
      try {
        return LOOKUP.findStatic(Builtin.class, builtin, MethodType.methodType(returnType, type1, type2));
      } catch (NoSuchMethodException | IllegalAccessException e) {
        throw new AssertionError(e);
      }
    });
  }

  private static MethodHandle resolveUnaryBuiltin(String builtin, Class<?> returnType, Class<?> type) {
    var mangled = builtin + "/" + type.getName();
    return BUILTIN_MAP.computeIfAbsent(mangled, _ -> {
      try {
        return LOOKUP.findStatic(Builtin.class, builtin, MethodType.methodType(returnType, type));
      } catch (NoSuchMethodException | IllegalAccessException e) {
        throw new AssertionError(e);
      }
    });
  }

  private static void checkOperandIsNumber(Class<?> type) {
    if (!(Number.class.isAssignableFrom(type))) {
      throw new Failure("operand is not a number");
    }
  }

  private static void checkOperandsAreNumber(Class<?> type1, Class<?> type2) {
    if (!(Number.class.isAssignableFrom(type1))) {
      throw new Failure("left operand is not a number");
    }
    if (!(Number.class.isAssignableFrom(type2))) {
      throw new Failure("right operand is not a number");
    }
  }

  private static String add(Object a, Object b) {
    return String.valueOf(a).concat(String.valueOf(b));
  }
  private static int add(int a, int b) {
    return a + b;
  }
  private static double add(double a, double b) {
    return a + b;
  }
  private static int sub(int a, int b) {
    return a - b;
  }
  private static double sub(double a, double b) {
    return a - b;
  }
  private static int mul(int a, int b) {
    return a * b;
  }
  private static double mul(double a, double b) {
    return a * b;
  }
  private static Number div(int a, int b) {
    var result = (double) a / b;
    if ((double) (int) result == result && !Double.isInfinite(result)) {
      return (int) result;
    }
    return result;
  }
  private static double div(double a, double b) {
    return a / b;
  }
  private static int mod(int a, int b) {
    return a % b;
  }
  private static double mod(double a, double b) {
    return a % b;
  }
  private static boolean lt(String a, String b) {
    return a.compareTo(b) < 0;
  }
  private static boolean lt(int a, int b) {
    return a < b;
  }
  private static boolean lt(double a, double b) {
    return a < b;
  }
  private static boolean le(String a, String b) {
    return a.compareTo(b) <= 0;
  }
  private static boolean le(int a, int b) {
    return a <= b;
  }
  private static boolean le(double a, double b) {
    return a <= b;
  }
  private static boolean gt(String a, String b) {
    return a.compareTo(b) > 0;
  }
  private static boolean gt(int a, int b) {
    return a > b;
  }
  private static boolean gt(double a, double b) {
    return a > b;
  }
  private static boolean ge(String a, String b) {
    return a.compareTo(b) >= 0;
  }
  private static boolean ge(int a, int b) {
    return a >= b;
  }
  private static boolean ge(double a, double b) {
    return a >= b;
  }
  private static boolean eq(String a, String b) {
    return a.equals(b);
  }
  private static boolean eq(boolean a, boolean b) {
    return a == b;
  }
  private static boolean eq(int a, int b) {
    return a == b;
  }
  private static boolean eq(double a, double b) {
    return a == b;
  }
  private static boolean eq(Object a, Object b) {
    return a.equals(b);
  }
  private static boolean ne(String a, String b) {
    return !a.equals(b);
  }
  private static boolean ne(boolean a, boolean b) {
    return a != b;
  }
  private static boolean ne(int a, int b) {
    return a != b;
  }
  private static boolean ne(double a, double b) {
    return a != b;
  }
  private static boolean ne(Object a, Object b) {
    return !a.equals(b);
  }

  private static MethodHandle resolveBinaryOpPlus(Class<?> type1, Class<?> type2) {
    if (type1 == String.class || type2 == String.class) {
      return resolveBinaryBuiltin("add", String.class,Object.class, Object.class);
    }
    return resolveBinaryNumber("add", type1, type2);
  }

  private static MethodHandle resolveBinaryOpDiv(Class<?> type1, Class<?> type2) {
    checkOperandsAreNumber(type1, type2);
    if (type1 == Double.class || type2 == Double.class) {
      return resolveBinaryBuiltin("div", double.class,double.class, double.class);
    }
    if (type1 == Integer.class && type2 == Integer.class) {
      return resolveBinaryBuiltin("div", Number.class,int.class, int.class);
    }
    throw new Failure(type1.getName() + " div " + type2.getName());
  }

  private static MethodHandle resolveBinaryNumber(String name, Class<?> type1, Class<?> type2) {
    checkOperandsAreNumber(type1, type2);
    if (type1 == Double.class || type2 == Double.class) {
      return resolveBinaryBuiltin(name, double.class,double.class, double.class);
    }
    if (type1 == Integer.class && type2 == Integer.class) {
      return resolveBinaryBuiltin(name, int.class,int.class, int.class);
    }
    throw new Failure(type1.getName() + " " + name + " " + type2.getName());
  }

  private static MethodHandle resolveBinaryRelation(String name, Class<?> type1, Class<?> type2) {
    if (type1 == String.class && type2 == String.class) {
      return resolveBinaryBuiltin(name, boolean.class,String.class, String.class);
    }
    checkOperandsAreNumber(type1, type2);
    if (type1 == Double.class || type2 == Double.class) {
      return resolveBinaryBuiltin(name, boolean.class,double.class, double.class);
    }
    if (type1 == Integer.class && type2 == Integer.class) {
      return resolveBinaryBuiltin(name, boolean.class,int.class, int.class);
    }
    throw new Failure(type1.getName() + " " + name + " " + type2.getName());
  }

  // FIXME, should fallback to a generic path using Object.equals()
  private static MethodHandle resolveBinaryEquality(String name, Class<?> type1, Class<?> type2) {
    if ((type1 == Double.class && Number.class.isAssignableFrom(type2)) || (Number.class.isAssignableFrom(type1) && type2 == Double.class)) {
      return resolveBinaryBuiltin(name, boolean.class,double.class, double.class);
    }
    if (type1 != type2) {
      return MethodHandles.dropArguments(MethodHandles.constant(boolean.class, !name.equals("eq")), 0, type1, type2);
    }
    if (type1 == Boolean.class) {
      return resolveBinaryBuiltin(name, boolean.class,boolean.class, boolean.class);
    }
    if (type1 == String.class) {
      return resolveBinaryBuiltin(name, boolean.class,String.class, String.class);
    }
    if (type1 == Integer.class) {
      return resolveBinaryBuiltin(name, boolean.class,int.class, int.class);
    }
    return resolveBinaryBuiltin(name, boolean.class,Object.class, Object.class);
  }

  private static MethodHandle resolveBinaryOp(String opName, Class<?> type1, Class<?> type2) {
    var mh = switch (opName) {
      case "+" -> resolveBinaryOpPlus(type1, type2);
      case "-" -> resolveBinaryNumber("sub", type1, type2);
      case "*" -> resolveBinaryNumber("mul", type1, type2);
      case "/" -> resolveBinaryOpDiv(type1, type2);
      case "%" -> resolveBinaryNumber("mod", type1, type2);
      case "<" -> resolveBinaryRelation("lt", type1, type2);
      case "<=" -> resolveBinaryRelation("le", type1, type2);
      case ">" -> resolveBinaryRelation("gt", type1, type2);
      case ">=" -> resolveBinaryRelation("ge", type1, type2);
      case "===" -> resolveBinaryEquality("eq", type1, type2);
      case "!==" -> resolveBinaryEquality("ne", type1, type2);
      default -> throw new UnsupportedOperationException(type1.getName() + " " + opName + " " + type2.getName());
    };
    return mh.asType(MethodType.methodType(mh.type().returnType(), type1, type2));
  }

  private static int plus(int a) {
    return a;
  }
  private static double plus(double a) {
    return a;
  }
  private static int minus(int a) {
    return - a;
  }
  private static double minus(double a) {
    return - a;
  }

  private static MethodHandle resolveUnaryNumber(String name, Class<?> type) {
    checkOperandIsNumber(type);
    if (type == Double.class) {
      return resolveUnaryBuiltin(name, double.class,double.class);
    }
    if (type == Integer.class) {
      return resolveUnaryBuiltin(name, int.class,int.class);
    }
    throw new Failure(name + " " + type.getName());
  }

  private static MethodHandle resolveUnaryOp(String opName, Class<?> type) {
    var mh = switch (opName) {
      case "+" -> resolveUnaryNumber("plus", type);
      case "-" -> resolveUnaryNumber("minus", type);
      default -> throw new UnsupportedOperationException(opName + " " + type.getName());
    };
    return mh.asType(MethodType.methodType(mh.type().returnType(), type));
  }

  public static MethodHandle resolveBinary(String opName, Class<?> type1, Class<?> type2) {
    var mangled = type1.getName() + opName + type2.getName();
    return VARIANT_MAP.computeIfAbsent(mangled, _ -> resolveBinaryOp(opName, type1, type2).asType(MethodType.methodType(Object.class, Object.class, Object.class)));
  }

  public static MethodHandle resolveUnary(String opName, Class<?> type) {
    var mangled = opName + type.getName();
    return VARIANT_MAP.computeIfAbsent(mangled, _ -> resolveUnaryOp(opName, type).asType(MethodType.methodType(Object.class, Object.class)));
  }
}
