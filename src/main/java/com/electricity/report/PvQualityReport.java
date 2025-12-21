package com.electricity.report;

import com.electricity.config.PvTask4Config;
import com.electricity.models.DataRow;
import com.electricity.service.StatsService;

import java.util.DoubleSummaryStatistics;
import java.util.List;

public final class PvQualityReport {

  public static void print(List<DataRow> rows, PvTask4Config cfg) {
    if (rows == null || rows.isEmpty()) {
      return;
    }

    var m1Missing = countNaN(rows, DataRow::pvMod1);
    var m2Missing = countNaN(rows, DataRow::pvMod2);
    var m3Missing = countNaN(rows, DataRow::pvMod3);

    var m1Out = countOutliersZ(rows, DataRow::pvMod1, cfg);
    var m2Out = countOutliersZ(rows, DataRow::pvMod2, cfg);
    var m3Out = countOutliersZ(rows, DataRow::pvMod3, cfg);

    var negative1 = countNegative(rows, DataRow::pvMod1);
    var negative2 = countNegative(rows, DataRow::pvMod2);
    var negative3 = countNegative(rows, DataRow::pvMod3);

    var inconsistentDay = rows.stream()
                            .filter(r -> isDaylight(r, cfg))
                            .filter(r -> Double.isNaN(r.pvMod1()) &&
                                           !Double.isNaN(r.pvMod2()) &&
                                           !Double.isNaN(r.pvMod3()))
                            .count();

    System.out.printf("""
        === PV Quality Report ===
        Rows: %d

        Missing values
          pv_mod1: %d
          pv_mod2: %d
          pv_mod3: %d

        Negative values (physically invalid)
          pv_mod1: %d
          pv_mod2: %d
          pv_mod3: %d

        Outliers (daylight-only z-score > %.2f)
          pv_mod1: %d
          pv_mod2: %d
          pv_mod3: %d

        Inconsistency examples
          pv_mod1 missing while pv_mod2 and pv_mod3 present (daylight): %d
        """,
      rows.size(),
      m1Missing, m2Missing, m3Missing,
      negative1, negative2, negative3,
      cfg.outlierZScore(), m1Out, m2Out, m3Out,
      inconsistentDay
    );
  }

  public static void printDaylightOnly(List<DataRow> rows, PvTask4Config cfg) {
    var daylight = rows.stream()
                     .filter(r -> {
                       var rad = r.shortwaveRadiation();
                       return rad != null && !Double.isNaN(rad) && rad > cfg.daylightRadiationThreshold();
                     })
                     .toList();

    var table = List.of(
      StatsService.labeled("PV_mod1 (daylight)", daylight, DataRow::pvMod1),
      StatsService.labeled("PV_mod2 (daylight)", daylight, DataRow::pvMod2),
      StatsService.labeled("PV_mod3 (daylight)", daylight, DataRow::pvMod3)
    );

    System.out.println("PV MODULE QUALITY (DAYLIGHT ONLY)");
    StatsService.printReport(table);
  }

  private static boolean isDaylight(DataRow r, PvTask4Config cfg) {
    var rad = r.shortwaveRadiation();
    return rad != null && !Double.isNaN(rad) && rad > cfg.daylightRadiationThreshold();
  }

  private static long countNaN(List<DataRow> rows, java.util.function.ToDoubleFunction<DataRow> f) {
    return rows.stream().mapToDouble(f).filter(Double::isNaN).count();
  }

  private static long countNegative(List<DataRow> rows, java.util.function.ToDoubleFunction<DataRow> f) {
    return rows.stream().mapToDouble(f).filter(v -> !Double.isNaN(v) && v < 0.0).count();
  }

  private static long countOutliersZ(List<DataRow> rows, java.util.function.ToDoubleFunction<DataRow> f, PvTask4Config cfg) {
    var stats = daylightStats(rows, f, cfg);
    if (stats.n() < 3) {
      return 0;
    }

    var mean = stats.mean();
    var std = stats.std();
    if (std <= 0) {
      return 0;
    }

    return rows.stream()
             .filter(r -> isDaylight(r, cfg))
             .mapToDouble(f)
             .filter(v -> !Double.isNaN(v))
             .filter(v -> Math.abs((v - mean) / std) > cfg.outlierZScore())
             .count();
  }

  private static DayStats daylightStats(List<DataRow> rows, java.util.function.ToDoubleFunction<DataRow> f, PvTask4Config cfg) {
    var values = rows.stream()
                   .filter(r -> isDaylight(r, cfg))
                   .mapToDouble(f)
                   .filter(v -> !Double.isNaN(v) && v >= 0)
                   .toArray();

    if (values.length == 0) {
      return new DayStats(0, Double.NaN, Double.NaN);
    }

    var dss = new DoubleSummaryStatistics();
    for (var v : values) dss.accept(v);

    var mean = dss.getAverage();
    var ss = 0.0;
    for (var v : values) ss += (v - mean) * (v - mean);
    var std = Math.sqrt(ss / Math.max(1, values.length - 1));

    return new DayStats(values.length, mean, std);
  }

  private record DayStats(int n, double mean, double std) {}

  private PvQualityReport() {}
}
