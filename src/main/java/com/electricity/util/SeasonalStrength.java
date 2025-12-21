package com.electricity.util;

import com.electricity.models.DataRow;

import java.time.Month;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public final class SeasonalStrength {

  /**
   * Simple seasonal strength: average absolute seasonal magnitude per month.
   * Higher value => seasonality contributes more strongly to demand that month.
   */
  public static Map<Month, Double> byMonth(ClassicalDecomposition.Result dec, List<DataRow> rows) {
    var sumAbs = new EnumMap<Month, Double>(Month.class);
    var count = new EnumMap<Month, Integer>(Month.class);

    Arrays.stream(Month.values()).forEach(m -> {
      sumAbs.put(m, 0.0);
      count.put(m, 0);
    });

    int n = Math.min(rows.size(), dec.seasonal().length);

    IntStream.range(0, n).forEach(i -> {
      double s = dec.seasonal()[i];
      if (Double.isNaN(s)) return;
      Month m = rows.get(i).timestamp().getMonth();
      sumAbs.put(m, sumAbs.get(m) + Math.abs(s));
      count.put(m, count.get(m) + 1);
    });

    var out = new EnumMap<Month, Double>(Month.class);
    Arrays.stream(Month.values()).forEach(m -> {
      int c = count.get(m);
      out.put(m, c == 0 ? Double.NaN : (sumAbs.get(m) / c));
    });
    return out;
  }

  private SeasonalStrength() {}
}
