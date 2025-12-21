package com.electricity.util;

import com.electricity.models.DataRow;

import java.util.List;

public final class ClassicalDecomposition {

  public record Settings(int seasonalPeriodHours, int trendWindowHours) {
    public static Settings defaults() {
      return new Settings(24, 24 * 7);
    }
  }

  public record Result(double[] observed, double[] trend, double[] seasonal, double[] residual) {}

  public static Result decompose(List<DataRow> rows, Settings s) {
    double[] y = rows.stream().mapToDouble(DataRow::demand).toArray();

    double[] trend = MovingAverage.centered(y, s.trendWindowHours());
    double[] detrended = subtract(y, trend);

    double[] seasonal = Seasonality.meanByHourOfDay(detrended, rows, s.seasonalPeriodHours());
    double[] residual = subtract(detrended, seasonal);

    return new Result(y, trend, seasonal, residual);
  }

  private static double[] subtract(double[] a, double[] b) {
    int n = Math.min(a.length, b.length);
    double[] out = new double[n];
    for (int i = 0; i < n; i++) {
      double ai = a[i];
      double bi = b[i];
      out[i] = (Double.isNaN(ai) || Double.isNaN(bi)) ? Double.NaN : (ai - bi);
    }
    return out;
  }

  private ClassicalDecomposition() {}
}
