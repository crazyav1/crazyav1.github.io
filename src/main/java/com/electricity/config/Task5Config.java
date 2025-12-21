package com.electricity.config;

import java.time.Duration;

public record Task5Config(
  Duration timeSeriesWindow,
  int timeSeriesWidth,
  int timeSeriesHeight,
  int histWidth,
  int histHeight,
  int scatterWidth,
  int scatterHeight,
  int histogramBins,
  double normalityAlpha,
  double logEps,
  double comfortTempC
) {
  public static Task5Config defaults() {
    return new Task5Config(
      Duration.ofDays(14),
      1100, 650,
      900, 600,
      900, 600,
      30,
      0.05,
      1e-3,
      18.0
    );
  }
}
