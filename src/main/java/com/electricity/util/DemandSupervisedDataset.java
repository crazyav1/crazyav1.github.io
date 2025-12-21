package com.electricity.util;

import com.electricity.models.DataRow;

import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;

public final class DemandSupervisedDataset {

  private final float[] xFlat;     // row-major
  private final double[] y;
  private final int rows;
  private final int cols;

  private DemandSupervisedDataset(float[] xFlat, double[] y, int rows, int cols) {
    this.xFlat = xFlat;
    this.y = y;
    this.rows = rows;
    this.cols = cols;
  }

  public static DemandSupervisedDataset of(float[] xFlat, double[] y, int rows, int cols) {
    return new DemandSupervisedDataset(xFlat, y, rows, cols);
  }


  public static DemandSupervisedDataset build(List<DataRow> rows,
                                              int[] lags,
                                              int[] rollWindows) {
    int maxLag = Arrays.stream(lags).max().orElse(1);
    int maxRoll = Arrays.stream(rollWindows).max().orElse(1);
    int warmup = Math.max(maxLag, maxRoll);

    int n = rows.size() - warmup;
    int features = lags.length + rollWindows.length + 3; // hour, dow, weekend

    float[] x = new float[n * features];
    double[] y = new double[n];

    for (int i = warmup; i < rows.size(); i++) {
      int r = i - warmup;
      var row = rows.get(i);
      y[r] = row.demand();

      int c = 0;

      for (int lag : lags) {
        x[r * features + (c++)] = (float) rows.get(i - lag).demand();
      }

      for (int w : rollWindows) {
        x[r * features + (c++)] = (float) meanDemand(rows, i - w, i);
      }

      int hour = row.timestamp().getHour();
      DayOfWeek dow = row.timestamp().getDayOfWeek();
      int dowNum = dow.getValue();
      int weekend = (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) ? 1 : 0;

      x[r * features + (c++)] = hour;
      x[r * features + (c++)] = dowNum;
      x[r * features + (c)] = weekend;
    }

    return new DemandSupervisedDataset(x, y, n, features);
  }

  private static double meanDemand(List<DataRow> rows, int fromInclusive, int toExclusive) {
    double sum = 0.0;
    int c = 0;
    for (int i = fromInclusive; i < toExclusive; i++) {
      double v = rows.get(i).demand();
      if (Double.isNaN(v)) continue;
      sum += v;
      c++;
    }
    return c == 0 ? Double.NaN : sum / c;
  }

  public int size() { return rows; }
  public int numFeatures() { return cols; }
  public float[] xFlat() { return xFlat; }
  public double[] y() { return y; }
  public float[] yAsFloat() {
    float[] out = new float[y.length];
    for (int i = 0; i < y.length; i++) out[i] = (float) y[i];
    return out;
  }

  public DemandSupervisedDataset slice(int from, int to) {
    int newRows = to - from;
    float[] nx = new float[newRows * cols];
    double[] ny = new double[newRows];

    System.arraycopy(xFlat, from * cols, nx, 0, newRows * cols);
    System.arraycopy(y, from, ny, 0, newRows);

    return new DemandSupervisedDataset(nx, ny, newRows, cols);
  }
}
