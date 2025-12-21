package com.electricity.util;

import java.time.OffsetDateTime;
import java.util.List;

public final class DriftForecaster implements Forecaster {

  @Override
  public double[] forecastNext(List<Double> history, int steps, OffsetDateTime start) {
    int n = history.size();
    double first = history.getFirst();
    double last = history.get(n - 1);

    double slope = (n > 1) ? (last - first) / (n - 1) : 0.0;

    var out = new double[steps];
    for (int h = 1; h <= steps; h++) {
      out[h - 1] = last + h * slope;
    }
    return out;
  }
}
