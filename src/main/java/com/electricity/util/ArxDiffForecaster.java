package com.electricity.util;


import com.electricity.config.ArxConfig;
import com.electricity.models.ForecastExoRow;
import com.electricity.models.PastRow;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public final class ArxDiffForecaster implements Task10Forecaster {

  private final ArxConfig cfg;

  public ArxDiffForecaster(ArxConfig cfg) {
    this.cfg = cfg;
  }

  @Override
  public double[] forecast24(List<PastRow> history, List<ForecastExoRow> future24) {
    int p = cfg.order();

    var y = history.stream().mapToDouble(PastRow::demand).toArray();
    var dy = Differencing.diff1(y);

    int k = exoFeatureCount(cfg);
    var targets = new ArrayList<Double>();
    var rows = new ArrayList<double[]>();

    for (int t = p; t < dy.length; t++) {
      double yt = dy[t];
      if (!DoubleUtil.finite(yt)) continue;

      double[] x = new double[p + k];
      boolean ok = true;

      for (int j = 1; j <= p; j++) {
        double lag = dy[t - j];
        if (!DoubleUtil.finite(lag)) { ok = false; break; }
        x[j - 1] = lag;
      }
      if (!ok) continue;

      var r = history.get(t);
      ok = fillExo(x, p, r.timestamp(), r.temperature(), r.cloudCover(), r.windSpeed10m(), r.pressure(), cfg);
      if (!ok) continue;

      targets.add(yt);
      rows.add(x);
    }

    double[] beta = fitNoIntercept(targets, rows, p + k);

    var dyHist = dy.clone();
    double lastLevel = y[y.length - 1];

    var dyFc = new double[24];
    for (int h = 0; h < 24; h++) {
      double[] x = new double[p + k];

      for (int j = 1; j <= p; j++) {
        x[j - 1] = dyHist[dyHist.length - j];
      }

      var f = future24.get(h);
      boolean ok = fillExo(x, p, f.timestamp(), f.temperature(), f.cloudCover(), f.windSpeed10m(), f.pressure(), cfg);
      if (!ok) {
        dyFc[h] = 0.0;
      } else {
        dyFc[h] = dot(beta, x);
      }

      dyHist = append(dyHist, dyFc[h]);
    }

    return Differencing.integrateFrom(lastLevel, dyFc);
  }

  private static double[] append(double[] a, double v) {
    var out = java.util.Arrays.copyOf(a, a.length + 1);
    out[out.length - 1] = v;
    return out;
  }

  private static double[] fitNoIntercept(List<Double> y, List<double[]> x, int dim) {
    if (y.size() < Math.max(50, dim * 5)) {
      return new double[dim];
    }
    double[] yy = y.stream().mapToDouble(Double::doubleValue).toArray();
    double[][] xx = x.toArray(double[][]::new);

    var ols = new OLSMultipleLinearRegression();
    ols.setNoIntercept(true);
    ols.newSampleData(yy, xx);
    return ols.estimateRegressionParameters();
  }

  private static double dot(double[] a, double[] b) {
    double s = 0.0;
    for (int i = 0; i < a.length; i++) s += a[i] * b[i];
    return s;
  }

  private static int exoFeatureCount(ArxConfig cfg) {
    int c = 0;
    if (cfg.includeHour()) c += 1;
    if (cfg.includeWeekend()) c += 1;
    if (cfg.includeWeather()) c += 4;
    return c;
  }

  private static boolean fillExo(double[] x, int offset,
                                 java.time.OffsetDateTime ts,
                                 double temp, double cloud, double wind, double press,
                                 ArxConfig cfg) {

    int i = offset;

    if (cfg.includeHour()) {
      x[i++] = ts.getHour();
    }

    if (cfg.includeWeekend()) {
      DayOfWeek d = ts.getDayOfWeek();
      x[i++] = (d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY) ? 1.0 : 0.0;
    }

    if (cfg.includeWeather()) {
      if (!DoubleUtil.finite(temp) || !DoubleUtil.finite(cloud) || !DoubleUtil.finite(wind) || !DoubleUtil.finite(press)) return false;
      x[i++] = temp;
      x[i++] = cloud;
      x[i++] = wind;
      x[i] = press;
    }

    return true;
  }
}
