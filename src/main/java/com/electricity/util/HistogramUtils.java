package com.electricity.util;

import com.electricity.models.HistogramMethods;

import java.util.List;

public class HistogramUtils {
  public static int computeBins(List<Double> values, HistogramMethods method) {
    int n = values.size();
    if (n == 0) {
      return 1;
    }
    var a = values.stream().mapToDouble(Double::doubleValue).sorted().toArray();

    switch (method) {
      case STURGES:
        return Math.max(1, (int) Math.ceil(Math.log(n) / Math.log(2) + 1));
      case RICE:
        return Math.max(1, (int) Math.ceil(2 * Math.cbrt(n)));
      case FREEDMAN_DIACONIS:
        double q1 = quantile(a, 0.25);
        double q3 = quantile(a, 0.75);
        double iqr = q3 - q1;
        double binWidth = 2 * iqr / Math.cbrt(n);
        if (binWidth <= 0) {
          return computeBins(values, HistogramMethods.STURGES);
        }
        double bins = Math.ceil((a[a.length - 1] - a[0]) / binWidth);
        return Math.max(1, (int) bins);
      default:
        return 30;
    }
  }

  private static double quantile(double[] a, double p) {
    if (a.length == 1) {
      return a[0];
    }
    double idx = p * (a.length - 1);
    int lo = (int) Math.floor(idx);
    int hi = (int) Math.ceil(idx);
    if (lo == hi) return a[lo];
    double frac = idx - lo;
    return a[lo] * (1 - frac) + a[hi] * frac;
  }
}
