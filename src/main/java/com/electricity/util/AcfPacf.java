package com.electricity.util;

import java.util.Arrays;

public final class AcfPacf {

  public static double[] acf(double[] x, int maxLag) {
    var clean = Arrays.stream(x).filter(v -> !Double.isNaN(v)).toArray();
    int n = clean.length;
    if (n < maxLag + 2) return new double[maxLag + 1];

    double mean = Arrays.stream(clean).average().orElse(0.0);

    double denom = 0.0;
    for (double v : clean) {
      double d = v - mean;
      denom += d * d;
    }
    if (denom == 0) return new double[maxLag + 1];

    double[] acf = new double[maxLag + 1];
    acf[0] = 1.0;

    for (int lag = 1; lag <= maxLag; lag++) {
      double num = 0.0;
      for (int i = lag; i < n; i++) {
        num += (clean[i] - mean) * (clean[i - lag] - mean);
      }
      acf[lag] = num / denom;
    }
    return acf;
  }


  public static double[] pacf(double[] x, int maxLag) {
    var r = acf(x, maxLag);
    double[] pacf = new double[maxLag + 1];
    pacf[0] = 1.0;

    double[] phi = new double[maxLag + 1];
    double[] prevPhi = new double[maxLag + 1];

    double v = 1.0;

    for (int k = 1; k <= maxLag; k++) {
      double sum = 0.0;
      for (int j = 1; j < k; j++) {
        sum += prevPhi[j] * r[k - j];
      }

      double alpha = (r[k] - sum) / Math.max(1e-12, v);

      phi[k] = alpha;
      for (int j = 1; j < k; j++) phi[j] = prevPhi[j] - alpha * prevPhi[k - j];

      v *= (1.0 - alpha * alpha);

      pacf[k] = phi[k];

      System.arraycopy(phi, 0, prevPhi, 0, phi.length);
    }
    return pacf;
  }

  private AcfPacf() {}
}
