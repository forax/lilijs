package com.github.forax.lilijs;

import com.caoccao.javet.swc4j.Swc4j;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.enums.Swc4jMediaType;
import com.caoccao.javet.swc4j.enums.Swc4jParseMode;
import com.caoccao.javet.swc4j.exceptions.Swc4jCoreException;
import com.caoccao.javet.swc4j.options.Swc4jParseOptions;

final class Parser {
  public static ISwc4jAst parse(String code) {
    var options = new Swc4jParseOptions()
        //.setSpecifier(new URL("file:///foo.js"))
        .setMediaType(Swc4jMediaType.TypeScript)
        .setCaptureAst(true)
        .setParseMode(Swc4jParseMode.Script);
    try {
      return new Swc4j().parse(code, options).getProgram();
    } catch (Swc4jCoreException e) {
      throw new Failure(e);
    }
  }
}
