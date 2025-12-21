package com.electricity.util;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public final class Normality {

  public record Result(double jb, double pValue, boolean isNormal) {}

  public static Result jarqueBera(double[] data, double alpha) {
    var stats = new DescriptiveStatistics();

    for (double v : data) {
      if (!Double.isNaN(v)) stats.addValue(v);
    }

    int n = (int) stats.getN();
    if (n < 8) {
      return new Result(Double.NaN, Double.NaN, false);
    }

    double skew = stats.getSkewness();
    double kurt = stats.getKurtosis();

    double jb = (n / 6.0) * (skew * skew + 0.25 * kurt * kurt);

    var chi2 = new ChiSquaredDistribution(2);
    double pValue = 1.0 - chi2.cumulativeProbability(jb);

    return new Result(jb, pValue, pValue > alpha);
  }

  private Normality() {}
}
