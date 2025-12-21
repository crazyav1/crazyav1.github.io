package com.electricity.models;

public final class CsvIndex {
  public static int indexOf(String headerLine, String name) {
    var cols = headerLine.split(",", -1);
    for (int i = 0; i < cols.length; i++) {
      if (cols[i].trim().equalsIgnoreCase(name)) return i;
    }
    throw new IllegalArgumentException("Missing column: " + name);
  }
  private CsvIndex() {}
}
