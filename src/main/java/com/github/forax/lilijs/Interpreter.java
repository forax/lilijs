package com.github.forax.lilijs;

import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAstProgram;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.caoccao.javet.swc4j.options.Swc4jTranspileOptions;
import com.caoccao.javet.swc4j.outputs.Swc4jParseOutput;
import com.caoccao.javet.swc4j.outputs.Swc4jTranspileOutput;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;

public final class Interpreter {
  public enum Media {
    TYPESCRIPT,
    JAVASCRIPT
  }

  public static Object interpret(String code, Media media, PrintStream outStream) {
    requireNonNull(code);
    requireNonNull(media);
    requireNonNull(outStream);

    var swc4j = new Swc4j();

    ISwc4jAstProgram<?> program;
    if (media == Media.TYPESCRIPT) {  // need to be transpiled
      var options = new Swc4jTranspileOptions()
          //.setSpecifier(specifier)
          .setMediaType(Swc4jMediaType.TypeScript)
          .setCaptureAst(true)
          .setParseMode(Swc4jParseMode.Script);
      try {
        program = new Swc4j().transpile(code, options).getProgram();
      } catch (Swc4jCoreException e) {
        throw new Failure(e);
      }
    } else {  // only need to be parsed
      var options = new Swc4jParseOptions()
          //.setSpecifier(new URL("file:///foo.js"))
          .setMediaType(Swc4jMediaType.JavaScript)
          .setCaptureAst(true)
          .setParseMode(Swc4jParseMode.Script);
      try {
        program = swc4j.parse(code, options).getProgram();
      } catch (Swc4jCoreException e) {
        throw new Failure(e);
      }
    }

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
