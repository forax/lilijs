package com.github.forax.lilijs;

import com.caoccao.javet.swc4j.ast.interfaces.ISwc4jAst;

import java.util.HashMap;
import java.util.List;

import static java.util.Objects.requireNonNull;

final class Dict {
  private int functionId;
  private final HashMap<Integer, FnInfo> functionMap = new HashMap<>();

  record FnInfo(boolean toplevel,
                String name, List<String> parameters, ISwc4jAst body,
                HashMap<ISwc4jAst, Object> dataMap) {
    FnInfo {
      requireNonNull(name);
      requireNonNull(parameters);
      requireNonNull(body);
      // dataMap can be null
    }
  }

  public int encodeFnInfo(FnInfo fnInfo) {
    var id = ++functionId;
    functionMap.put(id, fnInfo);
    return id;
  }

  public FnInfo decodeFnInfo(int id) {
    return functionMap.remove(id);
  }
}
