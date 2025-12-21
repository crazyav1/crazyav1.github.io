package com.electricity.util;

import com.electricity.config.PvTask4Config;
import com.electricity.models.DataRow;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public final class PvMod1Imputation {

  public record ImputationResult(
    double[] originalPvMod1,
    double[] deletionPvMod1,
    int[] deletionIndexMap,
    double[] univariatePvMod1,
    double[] multivariatePvMod1
  ) {}

  public static ImputationResult runAll(List<DataRow> rows, PvTask4Config cfg) {
    var original = rows.stream().mapToDouble(DataRow::pvMod1).toArray();

    var deletion = deletionDataset(rows);
    var univariate = univariateLinearInterpolation(rows, cfg);
    var multivariate = multivariateOLS(rows, cfg);

    return new ImputationResult(
      original,
      deletion.values(),
      deletion.indexMap(),
      univariate,
      multivariate
    );
  }

  public static DeletionDataset deletionDataset(List<DataRow> rows) {
    var values = new ArrayList<Double>(rows.size());
    var idx = new ArrayList<Integer>(rows.size());

    IntStream.range(0, rows.size()).forEach(i -> {
      var v = rows.get(i).pvMod1();
      if (!Double.isNaN(v)) {
        values.add(v);
        idx.add(i);
      }
    });

    return new DeletionDataset(
      values.stream().mapToDouble(Double::doubleValue).toArray(),
      idx.stream().mapToInt(Integer::intValue).toArray()
    );
  }

  public record DeletionDataset(double[] values, int[] indexMap) {}

  public static double[] univariateLinearInterpolation(List<DataRow> rows, PvTask4Config cfg) {
    var out = rows.stream().mapToDouble(DataRow::pvMod1).toArray();

    return getDoubles(rows, cfg, out);
  }

  public static double[] multivariateOLS(List<DataRow> rows, PvTask4Config cfg) {
    var original = rows.stream().mapToDouble(DataRow::pvMod1).toArray();
    var out = original.clone();

    var model = CommonsOlsModel.fit(rows, cfg);

    IntStream.range(0, rows.size()).filter(i -> Double.isNaN(out[i])).forEach(i -> {
      var r = rows.get(i);
      if (isNotDaylight(r, cfg)) {
        out[i] = 0.0;
        return;
      }
      if (Double.isNaN(r.pvMod2()) || Double.isNaN(r.pvMod3())) {
        return;
      }
      Double radObj = r.shortwaveRadiation();
      Double cloudObj = r.cloudCover();
      if (radObj == null || cloudObj == null) return;
      double rad = radObj;
      double cloud = cloudObj;
      if (Double.isNaN(rad) || Double.isNaN(cloud)) return;
      var pred = model.predict(r.pvMod2(), r.pvMod3(), rad, cloud);
      out[i] = Math.max(0.0, pred);
    });

    var fallback = univariateFallbackFromArray(rows, out, cfg);
    IntStream.range(0, out.length)
      .filter(i -> Double.isNaN(out[i]))
      .forEach(i -> out[i] = fallback[i]);
    IntStream.range(0, out.length)
      .filter(i -> !Double.isNaN(out[i]))
      .forEach(i -> out[i] = Math.max(0.0, out[i]));

    return out;
  }

  private static double[] univariateFallbackFromArray(List<DataRow> rows, double[] base, PvTask4Config cfg) {
    var out = base.clone();

    return getDoubles(rows, cfg, out);
  }

  private static double[] getDoubles(List<DataRow> rows, PvTask4Config cfg, double[] out) {
    IntStream.range(0, out.length).filter(i -> Double.isNaN(out[i])).forEach(i -> {
      var r = rows.get(i);
      if (isNotDaylight(r, cfg)) {
        out[i] = 0.0;
        return;
      }
      var left = findLeft(out, i);
      var right = findRight(out, i);
      if (left >= 0 && right >= 0) {
        out[i] = linear(out[left], out[right], (double) (i - left) / (right - left));
      } else if (left >= 0) {
        out[i] = out[left];
      } else if (right >= 0) {
        out[i] = out[right];
      }
    });

    for (int i = 0; i < out.length; i++) {
      if (!Double.isNaN(out[i])) out[i] = Math.max(0.0, out[i]);
    }

    return out;
  }

  private static boolean isNotDaylight(DataRow r, PvTask4Config cfg) {
    var rad = r.shortwaveRadiation();
    if (rad == null) {
      return true;
    }
    if (Double.isNaN(rad)) {
      return true;
    }
    return !(rad > cfg.daylightRadiationThreshold());
  }

  private static int findLeft(double[] arr, int i) {
    for (int k = i - 1; k >= 0; k--) if (!Double.isNaN(arr[k])) return k;
    return -1;
  }

  private static int findRight(double[] arr, int i) {
    for (int k = i + 1; k < arr.length; k++) if (!Double.isNaN(arr[k])) return k;
    return -1;
  }

  private static double linear(double a, double b, double t) {
    return a * (1 - t) + b * t;
  }

  private PvMod1Imputation() {}
}
