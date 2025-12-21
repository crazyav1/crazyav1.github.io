package com.electricity.util;


import com.electricity.models.ArDemandModel;

import java.util.Arrays;

public final class TimeSeriesValidationAR {

  public static double rollingOneStepNRMSE(double[] y, ArDemandModel model, int minTrainSize) {
    int n = y.length;
    double[] pred = new double[n];
    Arrays.fill(pred, Double.NaN);

    for (int i = minTrainSize; i < n; i++) {
      if (Double.isNaN(y[i - 1]) || Double.isNaN(y[i])) continue;

      double[] train = Arrays.copyOfRange(y, 0, i);
      double[] fc = model.forecastLevels(train, 1);
      pred[i] = fc[0];
    }

    return Metrics.nrmse(y, pred, minTrainSize, n);
  }

  public static double walkForwardLastWeekDailyNRMSE(double[] y, ArDemandModel model) {
    int horizon = 24;
    int folds = 7;

    int n = y.length;
    int start = Math.max(0, n - folds * horizon);

    double se = 0.0;
    int c = 0;

    for (int fold = 0; fold < folds; fold++) {
      int foldStart = start + fold * horizon;
      int foldEnd = Math.min(foldStart + horizon, n);
      if (foldStart < 10) continue;

      double[] train = Arrays.copyOfRange(y, 0, foldStart);
      double[] fc = model.forecastLevels(train, foldEnd - foldStart);

      for (int i = foldStart; i < foldEnd; i++) {
        double a = y[i];
        double p = fc[i - foldStart];
        if (Double.isNaN(a) || Double.isNaN(p)) continue;
        double e = a - p;
        se += e * e;
        c++;
      }
    }

    double rmse = Math.sqrt(se / Math.max(1, c));
    double norm = Metrics.range(y, start, n);
    return rmse / Math.max(1e-12, norm);
  }

  private TimeSeriesValidationAR() {}
}
