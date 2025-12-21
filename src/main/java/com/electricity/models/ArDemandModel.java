package com.electricity.models;


import com.electricity.util.Differencing;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import java.util.Arrays;

public final class ArDemandModel {

  private final String name;
  private final int order;

  public ArDemandModel(String name, int order) {
    this.name = name;
    this.order = order;
  }

  public String name() {
    return name;
  }

  private static double[] fitArOnDiff(double[] dy, int order) {
    var yList = new java.util.ArrayList<Double>();
    var xList = new java.util.ArrayList<double[]>();

    for (int t = order; t < dy.length; t++) {
      boolean ok = !Double.isNaN(dy[t]);
      if (!ok) {
        continue;
      }

      var x = new double[order];
      for (int j = 1; j <= order; j++) {
        double lag = dy[t - j];
        if (Double.isNaN(lag)) {
          ok = false;
          break;
        }
        x[j - 1] = lag;
      }
      if (!ok) continue;

      yList.add(dy[t]);
      xList.add(x);
    }

    if (yList.size() < Math.max(20, order * 5)) {
      return new double[order];
    }

    double[] y = yList.stream().mapToDouble(Double::doubleValue).toArray();
    double[][] X = xList.toArray(double[][]::new);

    var ols = new OLSMultipleLinearRegression();
    ols.setNoIntercept(true);
    ols.newSampleData(y, X);
    return ols.estimateRegressionParameters();
  }

  private static double forecastNextDiff(double[] phi, double[] dyHist, int order) {
    double yhat = 0.0;
    for (int i = 0; i < order; i++) {
      yhat += phi[i] * dyHist[dyHist.length - 1 - i];
    }
    return yhat;
  }

  public double[] forecastLevels(double[] yTrain, int steps) {
    var dy = Differencing.diff1(yTrain);
    var phi = fitArOnDiff(dy, order);

    var dyHist = Arrays.copyOf(dy, dy.length);

    var dyFc = new double[steps];
    for (int k = 0; k < steps; k++) {
      double next = forecastNextDiff(phi, dyHist, order);
      dyFc[k] = next;
      dyHist = Arrays.copyOf(dyHist, dyHist.length + 1);
      dyHist[dyHist.length - 1] = next;
    }

    double lastLevel = yTrain[yTrain.length - 1];
    return Differencing.integrateFrom(lastLevel, dyFc);
  }
}
