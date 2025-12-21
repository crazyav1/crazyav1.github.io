package com.electricity.models;

import com.electricity.util.OutlierCleaner;

import java.util.Arrays;

public record Stats(
  long count,
  long missing,
  double min,
  double max,
  double mean,
  double variance,
  double std,
  double median,
  double p05,
  double p95
) {

  public static Stats of(double[] data) {
    var missing = Arrays.stream(data).filter(Double::isNaN).count();
    var clean = Arrays.stream(data).filter(d -> !Double.isNaN(d)).toArray();

    if (clean.length == 0) {
      return new Stats(
        0, missing,
        Double.NaN, Double.NaN,
        Double.NaN, Double.NaN, Double.NaN,
        Double.NaN, Double.NaN, Double.NaN
      );
    }

    Arrays.sort(clean);

    var min = clean[0];
    var max = clean[clean.length - 1];
    var mean = Arrays.stream(clean).average().orElse(Double.NaN);

    double ss = Arrays.stream(clean).map(v -> (v - mean) * (v - mean)).sum();

    double variance = ss / Math.max(1, clean.length - 1);
    double std = Math.sqrt(variance);

    return new Stats(
      clean.length,
      missing,
      min,
      max,
      mean,
      variance,
      std,
      percentileSorted(clean, 50),
      percentileSorted(clean, 5),
      percentileSorted(clean, 95)
    );
  }

  private static double percentileSorted(double[] sorted, double p) {
    if (sorted.length == 1) {
      return sorted[0];
    }
    return OutlierCleaner.percentile(sorted, p);
  }
}
