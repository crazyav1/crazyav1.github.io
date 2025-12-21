package com.electricity.util;

import java.util.Arrays;

public final class Differencing {

  public static double[] diff1(double[] y) {
    var dy = new double[y.length];
    Arrays.fill(dy, Double.NaN);
    for (int i = 1; i < y.length; i++) {
      double a = y[i], b = y[i - 1];
      dy[i] = (Double.isNaN(a) || Double.isNaN(b)) ? Double.NaN : (a - b);
    }
    return dy;
  }

  public static double[] integrateFrom(double lastLevel, double[] dyForecast) {
    var out = new double[dyForecast.length];
    double cur = lastLevel;
    for (int i = 0; i < dyForecast.length; i++) {
      double d = dyForecast[i];
      if (Double.isNaN(d)) {
        out[i] = Double.NaN;
      } else {
        cur = cur + d;
        out[i] = cur;
      }
    }
    return out;
  }

  private Differencing() {}
}
