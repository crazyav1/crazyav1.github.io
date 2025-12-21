package com.electricity.util;

import com.electricity.config.Task8Config;
import com.electricity.models.PastRow;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.util.List;

public final class XgbFeaturesExo {

  public static int numFeatures(Task8Config cfg, boolean includeExo) {
    int base = cfg.lags().length + cfg.rollWindows().length + 3;
    return includeExo ? base + 4 : base;
  }

  public static float[] featuresFor(
    List<PastRow> history,
    OffsetDateTime ts,
    double temp,
    double cloud,
    double wind,
    double press,
    Task8Config cfg,
    boolean includeExo
  ) {
    int[] lags = cfg.lags();
    int[] rolls = cfg.rollWindows();

    int p = numFeatures(cfg, includeExo);
    float[] x = new float[p];
    int c = 0;

    int n = history.size();

    for (int lag : lags) {
      double v = history.get(n - lag).demand();
      x[c++] = DoubleUtil.finite(v) ? (float) v : Float.NaN;
    }

    for (int w : rolls) {
      x[c++] = (float) meanDemand(history, n - w, n);
    }

    int hour = ts.getHour();
    var dow = ts.getDayOfWeek();
    int dowNum = dow.getValue();
    int weekend = (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) ? 1 : 0;

    x[c++] = hour;
    x[c++] = dowNum;
    x[c++] = weekend;

    if (includeExo) {
      x[c++] = DoubleUtil.finite(temp)  ? (float) temp  : Float.NaN;
      x[c++] = DoubleUtil.finite(cloud) ? (float) cloud : Float.NaN;
      x[c++] = DoubleUtil.finite(wind)  ? (float) wind  : Float.NaN;
      x[c++] = DoubleUtil.finite(press) ? (float) press : Float.NaN;
    }

    return x;
  }

  private static double meanDemand(List<PastRow> h, int from, int to) {
    double sum = 0.0;
    int c = 0;
    for (int i = from; i < to; i++) {
      double v = h.get(i).demand();
      if (!DoubleUtil.finite(v)) continue;
      sum += v;
      c++;
    }
    return c == 0 ? Double.NaN : sum / c;
  }

  private XgbFeaturesExo() {}
}
