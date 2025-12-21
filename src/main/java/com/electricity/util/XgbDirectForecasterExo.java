package com.electricity.util;

import com.electricity.config.Task8Config;
import com.electricity.models.ForecastExoRow;
import com.electricity.models.PastRow;
import ml.dmlc.xgboost4j.java.Booster;

import java.util.List;

public final class XgbDirectForecasterExo implements Task10Forecaster {

  private final Task8Config cfg;
  private final boolean includeExo;

  public XgbDirectForecasterExo(Task8Config cfg, boolean includeExo) {
    this.cfg = cfg;
    this.includeExo = includeExo;
  }

  @Override
  public double[] forecast24(List<PastRow> history, List<ForecastExoRow> future24) throws Exception {
    int steps = 24;
    var out = new double[steps];

    for (int h = 1; h <= steps; h++) {
      var ds = XgbHorizonDatasetExo.build(history, cfg, h, includeExo);

      var split = TimeSplits.lastFraction(ds.size(), cfg.holdoutFraction());
      var train = ds.slice(0, split.trainEnd());
      var valid = ds.slice(split.trainEnd(), ds.size());

      Booster booster = TrainXgb.fit(train, valid, cfg);

      var f = future24.get(h - 1);
      float[] feat = XgbFeaturesExo.featuresFor(history, f.timestamp(), f.temperature(), f.cloudCover(), f.windSpeed10m(), f.pressure(), cfg, includeExo);

      out[h - 1] = XgbPredict.predictOne(booster, feat, XgbFeaturesExo.numFeatures(cfg, includeExo));
    }

    return out;
  }
}
