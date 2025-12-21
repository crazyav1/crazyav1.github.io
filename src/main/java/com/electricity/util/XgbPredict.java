package com.electricity.util;

import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;

public final class XgbPredict {

  public static double[] predict(Booster booster, DemandSupervisedDataset ds) throws Exception {
    DMatrix mat = new DMatrix(ds.xFlat(), ds.size(), ds.numFeatures(), Float.NaN);
    float[][] p = booster.predict(mat);
    double[] out = new double[ds.size()];
    for (int i = 0; i < out.length; i++) {
      out[i] = p[i][0];
    }
    return out;
  }

  public static double predictOne(Booster booster, float[] features, int numFeatures) throws Exception {
    DMatrix mat = new DMatrix(features, 1, numFeatures, Float.NaN);
    float[][] p = booster.predict(mat);
    return p[0][0];
  }

  private XgbPredict() {}
}
