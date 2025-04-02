package com.github.forax.lilijs;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public final class Interpreter {
  public static Object interpret(String code, PrintStream outStream) {
    requireNonNull(code);
    requireNonNull(outStream);

    var program = Parser.parse(code);

    var global = new JSObject(null);
    global.register("NaN", Double.NaN);
    global.register("Infinity", Double.POSITIVE_INFINITY);
    global.register("undefined", RT.UNDEFINED);
    global.register("print", new JSFunction("print", (_, array) -> {
      outStream.println(Arrays.stream(array).map(String::valueOf).collect(Collectors.joining(" ")));
      return RT.UNDEFINED;
    }));
    var mh = CodeGen.createFunctionMH("main",  List.of(), program, 0, null, global);
    var function = new JSFunction("main", mh);
    return function.invoke(RT.UNDEFINED);
  }
}
