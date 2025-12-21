package com.electricity.util;

import com.electricity.models.DataRow;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public final class Seasonality {
  public static double[] meanByHourOfDay(double[] detrended, List<DataRow> rows, int periodHours) {
    double[] sum = new double[periodHours];
    int[] cnt = new int[periodHours];

    int n = Math.min(detrended.length, rows.size());

    IntStream.range(0, n).forEach(i -> {
      double v = detrended[i];
      if (Double.isNaN(v)) return;
      int h = rows.get(i).timestamp().getHour(); // 0..23
      int slot = h % periodHours;
      sum[slot] += v;
      cnt[slot]++;
    });

    double[] seasonalMean = new double[periodHours];
    Arrays.setAll(seasonalMean, k -> cnt[k] == 0 ? 0.0 : (sum[k] / cnt[k]));

    double mean = Arrays.stream(seasonalMean).sum();
    mean /= periodHours;
    for (int k = 0; k < periodHours; k++) {
      seasonalMean[k] -= mean;
    }

    double[] seasonal = new double[n];
    for (int i = 0; i < n; i++) {
      int h = rows.get(i).timestamp().getHour();
      int slot = h % periodHours;
      seasonal[i] = seasonalMean[slot];
    }

    return seasonal;
  }

  private Seasonality() {}
}
