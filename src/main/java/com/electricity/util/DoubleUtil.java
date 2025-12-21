package com.electricity.util;

public final class DoubleUtil {
  public static boolean finite(double v) {
    return !Double.isNaN(v) && !Double.isInfinite(v);
  }
  private DoubleUtil() {}
}
