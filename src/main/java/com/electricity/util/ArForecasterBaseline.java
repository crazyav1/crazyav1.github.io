package com.electricity.util;

import com.electricity.models.ArDemandModel;
import com.electricity.models.ForecastExoRow;
import com.electricity.models.PastRow;

import java.util.List;

public final class ArForecasterBaseline implements Task10Forecaster {

  private final ArDemandModel model;

  public ArForecasterBaseline(ArDemandModel model) {
    this.model = model;
  }

  @Override
  public double[] forecast24(List<PastRow> history, List<ForecastExoRow> future24) {
    double[] y = history.stream().mapToDouble(PastRow::demand).toArray();
    return model.forecastLevels(y, 24);
  }
}
