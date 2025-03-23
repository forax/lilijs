package com.github.forax.lilijs;

import java.util.AbstractList;
import java.util.Objects;

public final class JSArray extends AbstractList<Object> {
  private Object[] array;
  private int size;

  private JSArray(Object[] array, int size) {
    this.array = array;
    this.size = size;
  }

  public JSArray() {
    this(new Object[1], 0);
  }

  public JSArray wrap(Object... array) {
    return new JSArray(array, array.length);
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public Object get(int index) {
    Objects.checkIndex(index, size);
    return array[index];
  }
}
