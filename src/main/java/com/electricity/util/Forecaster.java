package com.electricity.util;

import java.time.OffsetDateTime;
import java.util.List;

public interface Forecaster {
  double[] forecastNext(List<Double> history, int steps, OffsetDateTime start) throws Exception;

  static Named named(String name, Forecaster f) { return new Named(name, f); }

  record Named(String name, Forecaster f) {}
}
