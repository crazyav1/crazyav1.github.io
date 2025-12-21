package com.electricity.util;

import com.electricity.models.ArDemandModel;

import java.time.OffsetDateTime;
import java.util.List;

public final class ArForecasterAdapter implements Forecaster {

  private final ArDemandModel model;

  public ArForecasterAdapter(ArDemandModel model) {
    this.model = model;
  }

  @Override
  public double[] forecastNext(List<Double> history, int steps, OffsetDateTime start) {
    double[] yTrain = history.stream().mapToDouble(Double::doubleValue).toArray();
    return model.forecastLevels(yTrain, steps);
  }
}
