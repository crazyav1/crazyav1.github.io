package com.electricity.util;

import java.util.Arrays;

public final class OutlierCleaner {

  public record Bounds(double lower, double upper) {}

  public static Bounds iqrBounds(double[] x) {
    var clean = Arrays.stream(x).filter(v -> !Double.isNaN(v)).sorted().toArray();
    if (clean.length < 8) return new Bounds(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    double q1 = percentile(clean, 25);
    double q3 = percentile(clean, 75);
    double iqr = q3 - q1;

    return new Bounds(
      q1 - 1.5 * iqr,
      q3 + 1.5 * iqr
    );
  }

  public static double percentile(double[] sorted, double p) {
    double idx = (p / 100.0) * (sorted.length - 1);
    int lo = (int) Math.floor(idx);
    int hi = (int) Math.ceil(idx);
    double w = idx - lo;
    return sorted[lo] * (1 - w) + sorted[hi] * w;
  }

  public static boolean[] iqrMask(double[] x) {
    var clean = Arrays.stream(x)
                  .filter(v -> !Double.isNaN(v))
                  .sorted()
                  .toArray();

    if (clean.length < 8) {
      return new boolean[x.length];
    }

    double q1 = percentile(clean, 25);
    double q3 = percentile(clean, 75);
    double iqr = q3 - q1;

    double lower = q1 - 1.5 * iqr;
    double upper = q3 + 1.5 * iqr;

    var keep = new boolean[x.length];
    for (int i = 0; i < x.length; i++) {
      double v = x[i];
      keep[i] = !Double.isNaN(v) && v >= lower && v <= upper;
    }
    return keep;
  }

  private OutlierCleaner() {}
}
