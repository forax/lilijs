package com.github.forax.lilijs;

import java.lang.invoke.SwitchPoint;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;

public final class JSObject extends AbstractMap<String, Object> {
  private static final class Layout {
    private final LinkedHashMap<String, Integer> slotMap;
    private final HashMap<String, Layout> forwardMap = new HashMap<>();

    private Layout(LinkedHashMap<String, Integer> slotMap) {
      this.slotMap = slotMap;
    }

    private int slot(String key) {
      return slotMap.getOrDefault(key, -1);
    }

    private Layout forward(String key) {
      return forwardMap.computeIfAbsent(key, k -> {
        var newSlotMap = new LinkedHashMap<>(slotMap);
        newSlotMap.put(k, newSlotMap.size());
        return new Layout(newSlotMap);
      });
    }
  }

  private static final Layout ROOT = new Layout(new LinkedHashMap<>());
  private static final Object[] EMPTY_ARRAY = new Object[0];

  private final JSObject proto;
  private Layout layout = ROOT;
  private Object[] array = EMPTY_ARRAY;
  private SwitchPoint switchPoint = new SwitchPoint();

  public JSObject(JSObject proto) {
    this.proto = proto;
  }

  public SwitchPoint switchPoint() {
    if (switchPoint != null) {
      return switchPoint;
    }
    return switchPoint = new SwitchPoint();  // lazy allocate
  }
  public Object layout() {
    return layout;
  }
  public int layoutSlot(String key) {
    return layout.slot(key);
  }
  public Object fastAccess(int slot) {
    return array[slot];
  }

  @Override
  public Object getOrDefault(Object key, Object defaultValue) {
    requireNonNull(key);
    if (!(key instanceof String name)) {
      return defaultValue;
    }
    return getOrDefault(name, defaultValue);
  }

  public Object lookup(String key, Object defaultValue) {
    requireNonNull(key);
    var slot = layout.slot(key);
    if (slot != -1) {
      return array[slot];
    }
    if (proto != null) {
      return proto.getOrDefault(key, defaultValue);
    }
    return defaultValue;
  }

  @Override
  public boolean containsKey(Object key) {
    if (!(key instanceof String name)) {
      return false;
    }
    return containsKey(name);
  }

  public boolean containsKey(String key) {
    if (layout.slotMap.containsKey(key)) {
      return true;
    }
    if (proto != null) {
      return proto.containsKey(key);
    }
    return false;
  }

  @Override
  public Object put(String key, Object value) {
    requireNonNull(key);
    requireNonNull(value);
    var slot = layout.slot(key);
    Object result;
    if (slot != -1) {
      result = array[slot];
      array[slot] = value;
    } else {
      layout = layout.forward(key);
      array = Arrays.copyOf(array, array.length + 1);
      array[array.length - 1] = value;
      result = null;
    }
    if (switchPoint != null) {
      SwitchPoint.invalidateAll(new SwitchPoint[]{ switchPoint });
    }
    switchPoint = null;
    return result;
  }

  public void register(String key, Object value) {
    requireNonNull(key);
    requireNonNull(value);
    put(key, value);
  }

  @Override
  public int size() {
    return array.length;
  }

  @Override
  public Set<Entry<String, Object>> entrySet() {
    var slotMap = layout.slotMap;
    return new AbstractSet<>() {
      @Override
      public int size() {
        return array.length;
      }

      @Override
      public Iterator<Entry<String, Object>> iterator() {
        var it = slotMap.entrySet().iterator();
        return new Iterator<Entry<String, Object>>() {
          @Override
          public boolean hasNext() {
            return it.hasNext();
          }

          @Override
          public Entry<String, Object> next() {
            var entry = it.next();
            return Map.entry(entry.getKey(), array[entry.getValue()]);
          }
        };
      }
    };
  }

  @Override
  public String toString() {
    var builder = new StringBuilder();
    toString(this, builder, Collections.newSetFromMap(new IdentityHashMap<>()));
    return builder.toString();
  }

  private static void toString(Object object, StringBuilder builder, Set<Object> seen) {
    if(object == null) {
      builder.append("null");
      return;
    }
    if (!seen.add(object)) {
      builder.append("...");
      return;
    }
    if (!(object instanceof JSObject jsObject)) {
      builder.append(object);
      return;
    }
    jsObject.layout.slotMap.forEach((key, slot) -> {
      builder.append("  ").append(key).append(": ");
      toString(jsObject.array[slot], builder, seen);
      builder.append("\n");
    });
    builder.append("  proto: ");
    toString(jsObject.proto, builder, seen);
    builder.append("\n");
    builder.append("}");
  }
}