package com.electricity.util;

import java.util.stream.IntStream;

public final class MovingAverage {

  public static double[] centered(double[] y, int window) {
    if (window < 3) {
      return y.clone();
    }

    int n = y.length;
    double[] out = new double[n];

    int half = window / 2;

    double[] prefix = new double[n + 1];
    int[] count = new int[n + 1];

    IntStream.range(0, n).forEach(i -> {
      double v = y[i];
      prefix[i + 1] = prefix[i] + (Double.isNaN(v) ? 0.0 : v);
      count[i + 1] = count[i] + (Double.isNaN(v) ? 0 : 1);
    });

    IntStream.range(0, n).forEach(i -> {
      int left = i - half;
      int right = i + half;
      if (left < 0 || right >= n) {
        out[i] = Double.NaN;
        return;
      }
      double sum = prefix[right + 1] - prefix[left];
      int c = count[right + 1] - count[left];
      out[i] = (c == 0) ? Double.NaN : (sum / c);
    });

    return out;
  }

  private MovingAverage() {}
}
