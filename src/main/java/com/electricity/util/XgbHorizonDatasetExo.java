package com.electricity.util;

import com.electricity.config.Task8Config;
import com.electricity.models.PastRow;

import java.util.List;

public final class XgbHorizonDatasetExo {

  public static DemandSupervisedDataset build(
    List<PastRow> history,
    Task8Config cfg,
    int horizonStep,
    boolean includeExo
  ) {
    int warmup = Math.max(max(cfg.lags()), max(cfg.rollWindows()));
    int n = history.size() - warmup - horizonStep;
    int f = XgbFeaturesExo.numFeatures(cfg, includeExo);

    float[] xFlat = new float[n * f];
    double[] y = new double[n];

    for (int i = warmup; i < warmup + n; i++) {
      y[i - warmup] = history.get(i + horizonStep).demand();

      var rowT = history.get(i);
      float[] feats = XgbFeaturesExo.featuresFor(
        history.subList(0, i + 1),
        rowT.timestamp(),
        rowT.temperature(), rowT.cloudCover(), rowT.windSpeed10m(), rowT.pressure(),
        cfg,
        includeExo
      );

      System.arraycopy(feats, 0, xFlat, (i - warmup) * f, f);
    }

    return DemandSupervisedDataset.of(xFlat, y, n, f);
  }

  private static int max(int[] a) {
    int m = a[0];
    for (int v : a) m = Math.max(m, v);
    return m;
  }

  private XgbHorizonDatasetExo() {}
}
