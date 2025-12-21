package com.electricity.config;

import java.time.Duration;

public record PvTask4Config(
  double daylightRadiationThreshold,
  double outlierZScore,
  Duration representativeWindow,
  int representativeDays
) {
  public static PvTask4Config defaults() {
    return new PvTask4Config(
      1e-9,                 // daylight if radiation > 0
      4.0,                  // conservative outlier threshold
      Duration.ofDays(3),
      3
    );
  }
}
