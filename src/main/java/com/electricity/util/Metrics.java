package com.electricity.util;

public final class Metrics {
  public static double mae(double[] actual, double[] pred) {
    double sum = 0.0;
    int n = 0;
    for (int i = 0; i < actual.length; i++) {
      double a = actual[i];
      double p = pred[i];
      if (Double.isNaN(a) || Double.isNaN(p)) continue;
      sum += Math.abs(a - p);
      n++;
    }
    return n == 0 ? Double.NaN : sum / n;
  }

  public static double nrmse(double[] actual, double[] pred, int fromInclusive, int toExclusive) {
    double se = 0.0;
    int n = 0;

    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;

    for (int i = fromInclusive; i < toExclusive; i++) {
      double a = actual[i];
      double p = pred[i];

      if (Double.isNaN(a) || Double.isNaN(p)) {
        continue;
      }

      double e = a - p;
      se += e * e;
      n++;

      if (a < min) {
        min = a;
      }
      if (a > max) {
        max = a;
      }
    }

    if (n == 0) {
      return Double.NaN;
    }

    double rmse = Math.sqrt(se / n);
    double range = max - min;
    if (!(range > 0.0)) {
      return Double.NaN;
    }

    return rmse / range;
  }

  public static double range(double[] x, int fromInclusive, int toExclusive) {
    double min = Double.POSITIVE_INFINITY;
    double max = Double.NEGATIVE_INFINITY;

    for (int i = fromInclusive; i < toExclusive; i++) {
      double v = x[i];
      if (Double.isNaN(v)) {
        continue;
      }
      if (v < min) {
        min = v;
      }
      if (v > max) {
        max = v;
      }
    }

    if (min == Double.POSITIVE_INFINITY) {
      return Double.NaN;
    }
    return max - min;
  }

  private Metrics() {}
}
