package com.electricity.config;

import com.electricity.util.TrainingWindow;

public record Task10Config(
  int days,
  int horizonHours,
  TrainingWindow trainingWindow,
  int rollingWindowHours,
  Task8Config xgb,
  ArxConfig arx
) {
  public static Task10Config defaults() {
    return new Task10Config(
      7,
      24,
      TrainingWindow.EXPANDING,
      24 * 30,
      Task8Config.defaults(),
      ArxConfig.defaults()
    );
  }
}
