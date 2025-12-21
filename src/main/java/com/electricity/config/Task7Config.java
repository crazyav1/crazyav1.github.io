package com.electricity.config;

public record Task7Config(int maxLag, int minTrainSize) {
  public static Task7Config defaults() {
    return new Task7Config(
      72,        // ACF/PACF up to 3 days
      24 * 14    // start evaluation after 2 weeks
    );
  }
}
