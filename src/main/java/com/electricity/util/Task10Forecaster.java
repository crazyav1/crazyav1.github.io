package com.electricity.util;

import com.electricity.models.ForecastExoRow;
import com.electricity.models.PastRow;

import java.util.List;

public interface Task10Forecaster {
  double[] forecast24(List<PastRow> history, List<ForecastExoRow> future24) throws Exception;
}
