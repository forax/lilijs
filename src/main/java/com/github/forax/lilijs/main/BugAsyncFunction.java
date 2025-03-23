package com.github.forax.lilijs.main;

import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;
import com.github.forax.lilijs.Interpreter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BugAsyncFunction {
  public static void main(String[] args) throws Swc4jCoreException {
    var code = """
        async function asyncTest() {
            return "Success";
        }
        """;
    var options = new Swc4jParseOptions()
        //.setSpecifier(new URL("file:///foo.js"))
        .setMediaType(Swc4jMediaType.JavaScript)
        .setCaptureAst(true)
        .setParseMode(Swc4jParseMode.Script);
    var swc4j = new Swc4j();
    var output = swc4j.parse(code, options);
    System.err.println(output.getProgram().toDebugString());
  }
}
