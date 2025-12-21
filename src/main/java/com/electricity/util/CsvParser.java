package com.electricity.util;

public final class CsvParser {
  public static double parseDoubleOrNaN(String s) {
    if (s == null) {
      return Double.NaN;
    }
    var t = s.trim();
    if (t.isEmpty()) {
      return Double.NaN;
    }
    return Double.parseDouble(t);
  }
  private CsvParser() {}
}
