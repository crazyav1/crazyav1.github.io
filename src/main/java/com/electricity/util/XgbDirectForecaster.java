package com.electricity.util;

import com.electricity.config.Task8Config;
import ml.dmlc.xgboost4j.java.Booster;

import java.time.OffsetDateTime;
import java.util.List;

public final class XgbDirectForecaster implements Forecaster {

  private final Task8Config cfg;

  public XgbDirectForecaster(Task8Config cfg) {
    this.cfg = cfg;
  }

  @Override
  public double[] forecastNext(List<Double> history, int steps, OffsetDateTime start) throws Exception {
    var preds = new double[steps];

    for (int h = 1; h <= steps; h++) {
      var ds = XgbHorizonDataset.build(history, start.minusHours(history.size() - 1), cfg, h);

      var split = TimeSplits.lastFraction(ds.size(), cfg.holdoutFraction());
      var train = ds.slice(0, split.trainEnd());
      var valid = ds.slice(split.trainEnd(), ds.size());

      Booster booster = TrainXgb.fit(train, valid, cfg);

      OffsetDateTime ts = start.plusHours(h - 1);
      float[] feat = XgbFeatures.featuresFor(history, ts, cfg.lags(), cfg.rollWindows());

      preds[h - 1] = XgbPredict.predictOne(booster, feat, XgbFeatures.numFeatures(cfg));
    }

    return preds;
  }
}
