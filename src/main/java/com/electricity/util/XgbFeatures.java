package com.electricity.util;

import com.electricity.config.Task8Config;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.util.List;

public final class XgbFeatures {

  public static int numFeatures(Task8Config cfg) {
    return cfg.lags().length + cfg.rollWindows().length + 3;
  }

  public static float[] featuresFor(List<Double> history, OffsetDateTime ts, int[] lags, int[] rolls) {
    int p = lags.length + rolls.length + 3;
    float[] x = new float[p];
    int c = 0;

    int n = history.size();

    for (int lag : lags) {
      Double v = history.get(n - lag);
      x[c++] = (v == null || Double.isNaN(v)) ? Float.NaN : v.floatValue();
    }

    for (int w : rolls) {
      x[c++] = (float) mean(history, n - w, n);
    }

    int hour = ts.getHour();
    DayOfWeek dow = ts.getDayOfWeek();
    int dowNum = dow.getValue();
    int weekend = (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) ? 1 : 0;

    x[c++] = hour;
    x[c++] = dowNum;
    x[c++] = weekend;

    return x;
  }

  private static double mean(List<Double> h, int from, int to) {
    double sum = 0.0;
    int c = 0;
    for (int i = from; i < to; i++) {
      double v = h.get(i);
      if (Double.isNaN(v)) continue;
      sum += v;
      c++;
    }
    return c == 0 ? Double.NaN : sum / c;
  }

  private XgbFeatures() {}
}
