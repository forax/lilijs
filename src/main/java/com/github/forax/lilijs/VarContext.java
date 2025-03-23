package com.github.forax.lilijs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.util.Objects.requireNonNull;

final class VarContext {
  private static final class VarInfo {
    private final int varIndex;  // index in outer context (parent)
    private int captureIndex;    // index in inner context

    public VarInfo(int varIndex, int captureIndex) {
      this.varIndex = varIndex;
      this.captureIndex = captureIndex;
    }

    @Override
    public String toString() {
      return "(" + varIndex + ", " + captureIndex + ")";
    }
  }

  static final class CaptureInfo {
    private final CaptureInfo parent;
    private final ArrayList<Integer> captures = new ArrayList<>();  // index: inner context, value:outer context
    private final HashMap<String, VarInfo> map;

    private CaptureInfo(HashMap<String, VarInfo> map, CaptureInfo parent) {
      this.map = requireNonNull(map);
      this.parent = parent;
    }

    public int addCapture(int varIndex) {
      var captureIndex = captures.size();
      captures.add(varIndex);
      return captureIndex;
    }

    public List<Integer> captures() {
      return captures;
    }
  }

  sealed interface VarData {
    enum Global implements VarData { INSTANCE }
    record Local(int index, VarContext.CaptureInfo captureInfo) implements VarData {}
    record Capture(int index) implements VarData {}
  }

  final CaptureInfo captureInfo;
  private final VarContext parent;
  int localCount;
  private final HashMap<String, Integer> localMap = new HashMap<>();

  private VarContext(CaptureInfo captureInfo, int localCount, VarContext parent) {
    this.captureInfo = requireNonNull(captureInfo);
    this.localCount = localCount;
    this.parent = parent;  // can be null
  }

  public VarContext() {
    this(new CaptureInfo(new HashMap<>(), null), 0, null);
  }

  public VarContext newLocalContext() {
    return new VarContext(captureInfo, localMap.size(), this);
  }

  private void debug() {
    System.err.println("captureInfo");
    for(var captureInfo = this.captureInfo; captureInfo != null; captureInfo = captureInfo.parent) {
      System.err.println("  captures " + captureInfo.captures);
      System.err.println("  map " + captureInfo.map);
      System.err.println("  ---");
    }
    System.err.println("context");
    for(var context = this; context != null; context = context.parent) {
      System.err.println("  local count " + context.localCount);
      System.err.println("  localMap " + context.localMap);
      System.err.println("  ---");
    }
    System.err.println();
  }

  private HashMap<String, VarInfo> flattenLocalVars() {
    var newCaptureMap = new HashMap<String, VarInfo>();
    for(var context = this; context != null; context = context.parent) {
      for(var entry : context.localMap.entrySet()) {
        var name = entry.getKey();
        var index = entry.getValue();
        newCaptureMap.putIfAbsent(name, new VarInfo(index, -1));
      }
    }
    return newCaptureMap;
  }

  public VarContext newCaptureContext() {
    var newCaptureMap = flattenLocalVars();
    return new VarContext(new CaptureInfo(newCaptureMap, captureInfo), 0, null);
  }

  public int varDef(String name) {
    requireNonNull(name);
    if (localMap.containsKey(name)) {
      return -1;  // already defined
    }
    var index = localCount++;
    localMap.put(name, index);
    return index;
  }

  private int localVar(String name) {
    for(var ctx = this; ctx != null; ctx = ctx.parent) {
      var index = ctx.localMap.get(name);
      if (index != null) {
        return index;
      }
    }
    return -1;
  }

  public VarData varUse(String name) {
    requireNonNull(name);
    var index = localVar(name);
    if (index != -1) {
      return new VarData.Local(index, captureInfo);
    }
    var captureIndex = findAndUpdateCaptureVar(captureInfo, name);
    if (captureIndex == -1) {
      return VarData.Global.INSTANCE;
    }
    return new VarData.Capture(captureIndex);
  }

  // recursive so we can add the capture variables at all levels
  private static int findAndUpdateCaptureVar(CaptureInfo captureInfo, String name) {
    var varInfo = captureInfo.map.get(name);
    if (varInfo != null) {
      var captureIndex = varInfo.captureIndex;
      if (captureIndex == -1) {
        captureIndex = captureInfo.addCapture(varInfo.varIndex);
        varInfo.captureIndex = captureIndex;
      }
      return captureIndex;
    }
    if (captureInfo.parent == null) {
      return -1;
    }
    var parentVarIndex = findAndUpdateCaptureVar(captureInfo.parent, name);
    if (parentVarIndex == -1) {
      return -1;
    }
    var captureIndex = captureInfo.addCapture(parentVarIndex);
    captureInfo.map.put(name, new VarInfo(parentVarIndex, captureIndex));
    return captureIndex;
  }
}
