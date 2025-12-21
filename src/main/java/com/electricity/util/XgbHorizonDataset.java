package com.electricity.util;

import com.electricity.config.Task8Config;

import java.time.OffsetDateTime;
import java.util.List;

public final class XgbHorizonDataset {

  public static DemandSupervisedDataset build(
    List<Double> history,
    OffsetDateTime firstTimestamp,
    Task8Config cfg,
    int horizonStep
  ) {
    int warmup = Math.max(max(cfg.lags()), max(cfg.rollWindows()));
    int n = history.size() - warmup - horizonStep;
    int f = XgbFeatures.numFeatures(cfg);

    float[] xFlat = new float[n * f];
    double[] y = new double[n];

    for (int i = warmup; i < warmup + n; i++) {
      OffsetDateTime ts = firstTimestamp.plusHours(i);

      y[i - warmup] = history.get(i + horizonStep);

      float[] feats = XgbFeatures.featuresFor(history.subList(0, i + 1), ts, cfg.lags(), cfg.rollWindows());
      System.arraycopy(feats, 0, xFlat, (i - warmup) * f, f);
    }

    return DemandSupervisedDataset.of(xFlat, y, n, f);
  }

  private static int max(int[] a) {
    int m = a[0];
    for (int v : a) m = Math.max(m, v);
    return m;
  }

  private XgbHorizonDataset() {}
}
