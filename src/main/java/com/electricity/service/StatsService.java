package com.electricity.service;

import com.electricity.models.DataRow;
import com.electricity.models.Stats;


import java.util.List;
import java.util.function.ToDoubleFunction;

public final class StatsService {

  public record LabeledStats(String label, Stats stats) {}

  public static List<LabeledStats> summarizeCore(List<DataRow> rows) {
    return List.of(
      labeled("Demand (kW)", rows, DataRow::demand),
      labeled("PV (kW)", rows, DataRow::pv),
      labeled("Price (EUR/kWh)", rows, DataRow::price)
    );
  }

  public static LabeledStats labeled(String label, List<DataRow> rows, ToDoubleFunction<DataRow> extractor) {
    var data = rows.stream().mapToDouble(extractor).toArray();
    return new LabeledStats(label, Stats.of(data));
  }

  public static void printReport(List<LabeledStats> stats) {
    System.out.println("Variable\tCount\tMissing\tMin\tMax\tMean\tStd\tMedian\tP05\tP95");
    stats.forEach(ls -> {
      var s = ls.stats();
      System.out.printf("%s\t%d\t%d\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f%n",
        ls.label(),
        s.count(), s.missing(),
        s.min(), s.max(),
        s.mean(), s.std(),
        s.median(), s.p05(), s.p95()
      );
    });
  }

  private StatsService() {}
}
