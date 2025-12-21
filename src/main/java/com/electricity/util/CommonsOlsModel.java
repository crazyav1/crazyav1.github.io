package com.electricity.util;

import com.electricity.config.PvTask4Config;
import com.electricity.models.DataRow;
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;


import java.util.ArrayList;
import java.util.List;

public final class CommonsOlsModel {

  private final double[] beta;

  private CommonsOlsModel(double[] beta) {
    this.beta = beta;
  }

  public static CommonsOlsModel fit(List<DataRow> rows, PvTask4Config cfg) {
    var xList = new ArrayList<double[]>();
    var yList = new ArrayList<Double>();

    for (var r : rows) {
      if (Double.isNaN(r.pvMod1())) {
        continue;
      }
      if (Double.isNaN(r.pvMod2()) || Double.isNaN(r.pvMod3())) {
        continue;
      }

      Double radObj = r.shortwaveRadiation();
      double cloudObj = r.cloudCover();
      if (radObj == null) {
        continue;
      }

      double rad = radObj;
      if (Double.isNaN(rad) || Double.isNaN(cloudObj)) {
        continue;
      }

      if (rad <= cfg.daylightRadiationThreshold()) {
        continue;
      }

      xList.add(new double[] { r.pvMod2(), r.pvMod3(), rad, cloudObj});
      yList.add(r.pvMod1());
    }

    if (xList.size() < 10) {
      return new CommonsOlsModel(new double[] { 0.0, 0.5, 0.5, 0.0, 0.0 });
    }

    var x = xList.toArray(double[][]::new);
    var y = yList.stream().mapToDouble(Double::doubleValue).toArray();

    var reg = new OLSMultipleLinearRegression();
    reg.setNoIntercept(false);
    reg.newSampleData(y, x);

    var beta = reg.estimateRegressionParameters();

    return new CommonsOlsModel(beta);
  }

  public double predict(double pv2, double pv3, double rad, double cloud) {
    return beta[0] + beta[1] * pv2 + beta[2] * pv3 + beta[3] * rad + beta[4] * cloud;
  }
}
