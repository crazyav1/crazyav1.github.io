package com.electricity.util;

public final class Correlation {

  public record Result(double value, int nPairs) {}

  public static Result pearson(double[] y, double[] x) {
    int n = Math.min(y.length, x.length);

    double sumX = 0, sumY = 0;
    int k = 0;

    for (int i = 0; i < n; i++) {
      double xi = x[i], yi = y[i];
      if (Double.isNaN(xi) || Double.isNaN(yi)) {
        continue;
      }
      sumX += xi;
      sumY += yi;
      k++;
    }

    if (k < 3) {
      return new Result(Double.NaN, k);
    }

    double meanX = sumX / k;
    double meanY = sumY / k;

    double sxx = 0, syy = 0, sxy = 0;
    for (int i = 0; i < n; i++) {
      double xi = x[i], yi = y[i];
      if (Double.isNaN(xi) || Double.isNaN(yi)) continue;
      double dx = xi - meanX;
      double dy = yi - meanY;
      sxx += dx * dx;
      syy += dy * dy;
      sxy += dx * dy;
    }

    if (sxx <= 0 || syy <= 0) {
      return new Result(Double.NaN, k);
    }
    return new Result(sxy / Math.sqrt(sxx * syy), k);
  }

  private Correlation() {}
}
