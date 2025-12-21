package com.electricity.util;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

public final class NaiveForecaster implements Forecaster {

  @Override
  public double[] forecastNext(List<Double> history, int steps, OffsetDateTime start) {
    var out = new double[steps];
    Arrays.fill(out, history.getLast());
    return out;
  }
}
