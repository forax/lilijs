package com.github.forax.lilijs;

import com.caoccao.javet.swc4j.ast.clazz.Swc4jAstClass;
import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstBlockStmt;
import com.caoccao.javet.swc4j.ast.stmt.Swc4jAstReturnStmt;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static java.util.Objects.requireNonNull;

final class Dict {
  private int infoId;
  private final HashMap<Integer, Info> infoMap = new HashMap<>();

  record Info(boolean toplevel,
              String name, List<String> parameters, ISwc4jAst bodyOrClass,
              CodeGen.DataMap dataMap) {
    Info {
      requireNonNull(name);
      requireNonNull(parameters);
      requireNonNull(bodyOrClass);
      assert bodyOrClass instanceof Swc4jAstClass ||
             bodyOrClass instanceof Swc4jAstBlockStmt ||
             bodyOrClass instanceof Swc4jAstReturnStmt;
      // dataMap can be null
    }
  }

  public Collection<Info> infos() {
    return infoMap.values();
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
