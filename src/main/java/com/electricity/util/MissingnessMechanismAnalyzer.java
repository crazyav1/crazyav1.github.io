package com.electricity.util;

import com.electricity.config.PvTask4Config;
import com.electricity.models.DataRow;

import java.util.List;

import static com.electricity.config.Constants.QUANTILE;

public final class MissingnessMechanismAnalyzer {

  public record MechanismEvidence(
    long totalRows,
    long pv1Missing,
    long pv1MissingNight,
    long pv1MissingDay,
    long pv1MissingWhenPv23PresentDay,
    long pv1MissingHighRadiationDay,
    long pv1MissingHighPv23Day
  ) {}

  public static MechanismEvidence analyzePvMod1(List<DataRow> rows, PvTask4Config cfg) {
    long total = rows.size();

    long missing = 0;
    long missingNight = 0;
    long missingDay = 0;

    long missingDayPv23Present = 0;

    long missingHighRad = 0;
    long missingHighPv23 = 0;

    var radThreshold = thresholdQuantile(rows, DataRow::shortwaveRadiation, QUANTILE);
    var pv23Threshold = thresholdQuantilePv23(rows, QUANTILE);

    for (var r : rows) {
      boolean isMissingPv1 = Double.isNaN(r.pvMod1());
      if (!isMissingPv1) {
        continue;
      }

      missing++;

      boolean daylight = isDaylight(r, cfg);
      if (daylight) missingDay++; else missingNight++;

      if (daylight && !Double.isNaN(r.pvMod2()) && !Double.isNaN(r.pvMod3())) {
        missingDayPv23Present++;
      }

      if (daylight) {
        Double rad = r.shortwaveRadiation();
        if (rad != null && !Double.isNaN(rad) && rad >= radThreshold) {
          missingHighRad++;
        }

        double pv23 = avgPv23(r);
        if (!Double.isNaN(pv23) && pv23 >= pv23Threshold) {
          missingHighPv23++;
        }
      }
    }

    return new MechanismEvidence(
      total, missing, missingNight, missingDay,
      missingDayPv23Present,
      missingHighRad,
      missingHighPv23
    );
  }

  public static void printReport(MechanismEvidence e) {
    System.out.printf("""
        === Missingness mechanism evidence for PV_mod1 ===
        Total rows: %d
        PV_mod1 missing: %d

        Split by day/night
          Missing at night (radiation <= threshold): %d
          Missing at day   (radiation >  threshold): %d

        Evidence for MAR
          Missing PV_mod1 while PV_mod2 & PV_mod3 present (daylight): %d

        Evidence suggesting MNAR (if these are high proportions)
          Missing during high radiation (>= p90 radiation, daylight): %d
          Missing when PV_mod2/3 are high (>= p90 PV23, daylight):    %d
        """,
      e.totalRows(),
      e.pv1Missing(),
      e.pv1MissingNight(),
      e.pv1MissingDay(),
      e.pv1MissingWhenPv23PresentDay(),
      e.pv1MissingHighRadiationDay(),
      e.pv1MissingHighPv23Day()
    );
  }

  private static boolean isDaylight(DataRow r, PvTask4Config cfg) {
    Double rad = r.shortwaveRadiation();
    return rad != null && !Double.isNaN(rad) && rad > cfg.daylightRadiationThreshold();
  }

  private static double avgPv23(DataRow r) {
    if (Double.isNaN(r.pvMod2()) || Double.isNaN(r.pvMod3())) {
      return Double.NaN;
    }
    return (r.pvMod2() + r.pvMod3()) / 2.0;
  }

  private static double thresholdQuantile(List<DataRow> rows, java.util.function.Function<DataRow, Double> f, double quantile) {
    var vals = rows.stream()
                 .map(f)
                 .filter(v -> v != null && !Double.isNaN(v))
                 .mapToDouble(Double::doubleValue)
                 .sorted()
                 .toArray();

    if (vals.length == 0) return Double.POSITIVE_INFINITY;
    int idx = (int) Math.floor(quantile * (vals.length - 1));
    return vals[idx];
  }

  private static double thresholdQuantilePv23(List<DataRow> rows, double quantile) {
    var vals = rows.stream()
                 .mapToDouble(MissingnessMechanismAnalyzer::avgPv23)
                 .filter(v -> !Double.isNaN(v))
                 .sorted()
                 .toArray();

    if (vals.length == 0) {
      return Double.POSITIVE_INFINITY;
    }
    int idx = (int) Math.floor(quantile * (vals.length - 1));
    return vals[idx];
  }

  private MissingnessMechanismAnalyzer() {}
}
