package com.electricity.config;

import com.electricity.util.TrainingWindow;

import static com.electricity.util.TrainingWindow.EXPANDING;

public record Task9Config(
  int days,
  int horizonHours,
  TrainingWindow trainingWindow,
  int rollingWindowHours,
  Task8Config xgbCfg
) {
  public static Task9Config defaults() {
    return new Task9Config(
      7,
      24,
      EXPANDING,
      24 * 30,
      Task8Config.defaults()
    );
  }

}
