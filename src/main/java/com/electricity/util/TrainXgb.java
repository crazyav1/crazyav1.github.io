package com.electricity.util;

import com.electricity.config.Task8Config;
import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoost;

import java.util.HashMap;
import java.util.Map;

public final class TrainXgb {

  public static Booster fit(DemandSupervisedDataset train,
                            DemandSupervisedDataset valid,
                            Task8Config cfg) throws Exception {
    DMatrix trainMat = new DMatrix(train.xFlat(), train.size(), train.numFeatures(), Float.NaN);
    trainMat.setLabel(train.yAsFloat());

    DMatrix validMat = new DMatrix(valid.xFlat(), valid.size(), valid.numFeatures(), Float.NaN);
    validMat.setLabel(valid.yAsFloat());

    Map<String, Object> params = new HashMap<>();
    params.put("objective", "reg:squarederror");
    params.put("eval_metric", "rmse");
    params.put("max_depth", cfg.maxDepth());
    params.put("eta", cfg.eta());
    params.put("subsample", cfg.subsample());
    params.put("colsample_bytree", cfg.colsampleByTree());
    params.put("min_child_weight", cfg.minChildWeight());
    params.put("lambda", cfg.lambdaL2());

    Map<String, DMatrix> watch = Map.of("train", trainMat, "valid", validMat);

    return XGBoost.train(trainMat, params, cfg.numRounds(), watch, null, null);
  }

  private TrainXgb() {}
}
