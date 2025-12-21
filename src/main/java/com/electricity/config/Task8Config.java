package com.electricity.config;

public record Task8Config(
  int[] lags,
  int[] rollWindows,
  double holdoutFraction,
  int numRounds,
  int maxDepth,
  double eta,
  double subsample,
  double colsampleByTree,
  double minChildWeight,
  double lambdaL2
) {
  public static Task8Config defaults() {
    return new Task8Config(
      new int[]{1, 2, 24, 48, 168},
      new int[]{3, 24},
      0.20,
      400,
      6,
      0.05,
      0.8,
      0.8,
      1.0,
      1.0
    );
  }
}
