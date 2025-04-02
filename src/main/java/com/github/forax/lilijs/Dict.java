package com.github.forax.lilijs;

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClass;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;

import java.util.HashMap;
import java.util.List;

import static java.util.Objects.requireNonNull;

final class Dict {
  private int infoId;
  private final HashMap<Integer, Info> infoMap = new HashMap<>();

  record Info(boolean toplevel,
              String name, List<String> parameters, ISwc4jAst body,
              HashMap<ISwc4jAst, Object> dataMap) {
    Info {
      requireNonNull(name);
      requireNonNull(parameters);
      requireNonNull(body);
      assert body instanceof Swc4jAstClass ||
             body instanceof Swc4jAstBlockStmt ||
             body instanceof Swc4jAstReturnStmt;
      // dataMap can be null
    }
  }

  public int encodeInfo(Info info) {
    var id = ++infoId;
    infoMap.put(id, info);
    return id;
  }

  public Info decodeInfo(int id) {
    return infoMap.remove(id);
  }
}
