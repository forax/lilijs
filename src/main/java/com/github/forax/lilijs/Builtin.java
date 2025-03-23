package com.github.forax.lilijs;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Objects;

import static java.lang.invoke.MethodType.genericMethodType;
import static java.lang.invoke.MethodType.methodType;

class Builtin {
  private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
  private static final HashMap<String, Stub> BUILTIN_MAP = new HashMap<>();
  private static final HashMap<String, Stub> VARIANT_MAP = new HashMap<>();

  public record Stub(boolean generic, MethodHandle mh) {
    public Stub asType(Class<?>... parameterType) {
      return new Stub(generic, mh.asType(methodType(mh.type().returnType(), parameterType)));
    }
    public Stub asGenericType() {
      return new Stub(generic, mh.asType(genericMethodType(mh.type().parameterCount())));
    }
  }

  private static Stub resolveBinaryBuiltin(String builtin, Class<?> returnType, Class<?> type1, Class<?> type2) {
    var mangled = type1.getName() + "/" + builtin + "/" + type2.getName();
    return BUILTIN_MAP.computeIfAbsent(mangled, _ -> {
      MethodHandle mh;
      try {
        mh = LOOKUP.findStatic(Builtin.class, builtin, methodType(returnType, type1, type2));
      } catch (NoSuchMethodException | IllegalAccessException e) {
        throw new AssertionError(e);
      }
      return new Stub(type1 == Object.class && type2 == Object.class, mh);
    });
  }

  private static Stub resolveUnaryBuiltin(String builtin, Class<?> returnType, Class<?> type) {
    var mangled = builtin + "/" + type.getName();
    return BUILTIN_MAP.computeIfAbsent(mangled, _ -> {
      MethodHandle mh;
      try {
        mh = LOOKUP.findStatic(Builtin.class, builtin, methodType(returnType, type));
      } catch (NoSuchMethodException | IllegalAccessException e) {
        throw new AssertionError(e);
      }
      return new Stub(type == Object.class, mh);
    });
  }

  private static void checkOperandIsNumber(Class<?> type) {
    if (!(Number.class.isAssignableFrom(type))) {
      throw new Failure("operand is not a number");
    }
  }

  private static void checkOperandsAreNumbers(Class<?> type1, Class<?> type2) {
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
    return Objects.equals(a, b);
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
    return !Objects.equals(a, b);
  }

  private static Stub resolveBinaryOpPlus(Class<?> type1, Class<?> type2) {
    if (type1 == String.class || type2 == String.class) {
      return resolveBinaryBuiltin("add", String.class,Object.class, Object.class);
    }
    return resolveBinaryNumber("add", type1, type2);
  }

  private static Stub resolveBinaryOpDiv(Class<?> type1, Class<?> type2) {
    checkOperandsAreNumbers(type1, type2);
    if (type1 == Double.class || type2 == Double.class) {
      return resolveBinaryBuiltin("div", double.class,double.class, double.class);
    }
    if (type1 == Integer.class && type2 == Integer.class) {
      return resolveBinaryBuiltin("div", Number.class,int.class, int.class);
    }
    throw new Failure(type1.getName() + " div " + type2.getName());
  }

  private static Stub resolveBinaryNumber(String name, Class<?> type1, Class<?> type2) {
    checkOperandsAreNumbers(type1, type2);
    if (type1 == Double.class || type2 == Double.class) {
      return resolveBinaryBuiltin(name, double.class,double.class, double.class);
    }
    if (type1 == Integer.class && type2 == Integer.class) {
      return resolveBinaryBuiltin(name, int.class,int.class, int.class);
    }
    throw new Failure(type1.getName() + " " + name + " " + type2.getName());
  }

  private static Stub resolveBinaryRelation(String name, Class<?> type1, Class<?> type2) {
    if (type1 == String.class && type2 == String.class) {
      return resolveBinaryBuiltin(name, boolean.class,String.class, String.class);
    }
    checkOperandsAreNumbers(type1, type2);
    if (type1 == Double.class || type2 == Double.class) {
      return resolveBinaryBuiltin(name, boolean.class,double.class, double.class);
    }
    if (type1 == Integer.class && type2 == Integer.class) {
      return resolveBinaryBuiltin(name, boolean.class,int.class, int.class);
    }
    throw new Failure(type1.getName() + " " + name + " " + type2.getName());
  }

  private static Stub resolveBinaryEquality(String name, Class<?> type1, Class<?> type2) {
    if ((type1 == Double.class && Number.class.isAssignableFrom(type2)) || (Number.class.isAssignableFrom(type1) && type2 == Double.class)) {
      return resolveBinaryBuiltin(name, boolean.class,double.class, double.class);
    }
    if (type1 != type2) {
      return new Stub(false, MethodHandles.dropArguments(MethodHandles.constant(boolean.class, !name.equals("eq")), 0, type1, type2));
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

  private static Stub resolveBinaryOp(String opName, Class<?> type1, Class<?> type2) {
    var stub = switch (opName) {
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
    return stub.asType(type1, type2);
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

  private static Stub resolveUnaryNumber(String name, Class<?> type) {
    checkOperandIsNumber(type);
    if (type == Double.class) {
      return resolveUnaryBuiltin(name, double.class,double.class);
    }
    if (type == Integer.class) {
      return resolveUnaryBuiltin(name, int.class,int.class);
    }
    throw new Failure(name + " " + type.getName());
  }

  private static Stub resolveUnaryOp(String opName, Class<?> type) {
    var stub = switch (opName) {
      case "+" -> resolveUnaryNumber("plus", type);
      case "-" -> resolveUnaryNumber("minus", type);
      default -> throw new UnsupportedOperationException(opName + " " + type.getName());
    };
    return stub.asType(type);
  }

  private static boolean truth(int a) {
    return a != 0;
  }
  private static boolean truth(double a) {
    return a != 0.0;
  }
  private static boolean truth(boolean b) {
    return b;
  }
  private static boolean truth(String s) {
    return !s.isEmpty();
  }
  static boolean truth(Object o) {
    // the first test is not strictly necessary but helps the VM
    return o != null && o != RT.UNDEFINED &&
        o instanceof Boolean b ? b :
          o instanceof Integer v ? v != 0 :
            o instanceof Double d ? d != 0.0 :
              o instanceof String s && !s.isEmpty();
  }

  private static Stub resolveTruthOp(Class<?> type) {
    if (type == Boolean.class) {
      return resolveUnaryBuiltin("truth", boolean.class,boolean.class);
    }
    if (type == Integer.class) {
      return resolveUnaryBuiltin("truth", boolean.class,int.class);
    }
    if (type == Double.class) {
      return resolveUnaryBuiltin("truth", boolean.class,double.class);
    }
    if (type == String.class) {
      return resolveUnaryBuiltin("truth", boolean.class,String.class);
    }
    return resolveUnaryBuiltin("truth", boolean.class,Object.class);
  }

  public static Stub resolveBinary(String opName, Class<?> type1, Class<?> type2) {
    var mangled = type1.getName() + opName + type2.getName();
    return VARIANT_MAP.computeIfAbsent(mangled, _ -> resolveBinaryOp(opName, type1, type2).asGenericType());
  }

  public static Stub resolveUnary(String opName, Class<?> type) {
    var mangled = opName + type.getName();
    return VARIANT_MAP.computeIfAbsent(mangled, _ -> resolveUnaryOp(opName, type).asGenericType());
  }

  public static Stub resolveTruth(Class<?> type) {
    var mangled = "truth/" + type.getName();
    return VARIANT_MAP.computeIfAbsent(mangled, _ -> resolveTruthOp(type).asType(Object.class));
  }
}
