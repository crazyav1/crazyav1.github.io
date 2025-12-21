package com.electricity.runner;

import com.electricity.reader.CsvReader;
import com.electricity.util.*;
import com.electricity.config.Task8Config;

import static com.electricity.config.Constants.TRAIN_PATH;

public final class Task8Runner {

  private static final Task8Config CFG = Task8Config.defaults();

  static void main() throws Exception {
    var rows = CsvReader.loadTrainTest(TRAIN_PATH);

    var dsAll = DemandSupervisedDataset.build(rows, CFG.lags(), CFG.rollWindows());

    var split = TimeSplits.lastFraction(dsAll.size(), CFG.holdoutFraction());
    var train = dsAll.slice(0, split.trainEnd());
    var test = dsAll.slice(split.trainEnd(), dsAll.size());

    var inner = TimeSplits.lastFraction(train.size(), CFG.holdoutFraction());
    var trainA = train.slice(0, inner.trainEnd());
    var validA = train.slice(inner.trainEnd(), train.size());

    var booster = TrainXgb.fit(trainA, validA, CFG);

    var predHoldout = XgbPredict.predict(booster, test);
    var nrmseHoldout = Metrics.nrmse(test.y(), predHoldout, 0, test.size());

    System.out.println("TASK 8 — XGBOOST HOLDOUT (last " + (int)(CFG.holdoutFraction() * 100) + "%)");
    System.out.printf("XGBoost NRMSE = %.6f%n", nrmseHoldout);

    var wf = WalkForwardXgb.evaluateLastWeekDaily(rows, CFG);
    System.out.println("TASK 8 - XGBOOST WALK-FORWARD (last week, daily folds)");
    System.out.printf("XGBoost NRMSE = %.6f%n", wf);

    System.out.println("COMPARE TO TASK 7 (AR(24) on diff)");
    System.out.println("AR(24) on diff walk-forward NRMSE = 0.173359");
    System.out.printf("XGBoost walk-forward NRMSE          = %.6f%n", wf);
  }

  private Task8Runner() {}
}
