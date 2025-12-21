package com.electricity.util;

import com.electricity.config.Task8Config;
import com.electricity.models.DataRow;

import java.util.List;

public final class WalkForwardXgb {

  public static double evaluateLastWeekDaily(List<DataRow> rows, Task8Config cfg) throws Exception {
    int horizon = 24;
    int folds = 7;

    var dsAll = DemandSupervisedDataset.build(rows, cfg.lags(), cfg.rollWindows());

    int end = dsAll.size();
    int start = Math.max(0, end - folds * horizon);

    double se = 0.0;
    int c = 0;

    for (int fold = 0; fold < folds; fold++) {
      int foldStart = start + fold * horizon;
      int foldEnd = Math.min(foldStart + horizon, end);

      var train = dsAll.slice(0, foldStart);
      var test = dsAll.slice(foldStart, foldEnd);

      var split = TimeSplits.lastFraction(train.size(), cfg.holdoutFraction());
      var trainA = train.slice(0, split.trainEnd());
      var validA = train.slice(split.trainEnd(), train.size());

      var booster = TrainXgb.fit(trainA, validA, cfg);

      var pred = XgbPredict.predict(booster, test);

      for (int i = 0; i < test.size(); i++) {
        double a = test.y()[i];
        double p = pred[i];
        if (Double.isNaN(a) || Double.isNaN(p)) continue;
        double e = a - p;
        se += e * e;
        c++;
      }
    }

    double rmse = Math.sqrt(se / Math.max(1, c));
    double norm = Metrics.range(dsAll.y(), start, end);
    return rmse / Math.max(1e-12, norm);
  }

  private WalkForwardXgb() {}
}
