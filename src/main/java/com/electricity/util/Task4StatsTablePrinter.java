package com.electricity.util;

import com.electricity.models.Stats;

import java.util.List;

public final class Task4StatsTablePrinter {

  public record Row(String method, Stats stats) {}

  public static void printReport(List<Row> rows) {
    System.out.println(String.join("\t",
      "Method",
      "Count",
      "Missing",
      "Mean",
      "Var",
      "Std",
      "Min",
      "Max",
      "Median",
      "P05",
      "P95"
    ));

    rows.forEach(r -> {
      var s = r.stats();
      System.out.println(String.join("\t",
        r.method(),
        Long.toString(s.count()),
        Long.toString(s.missing()),
        fmt(s.mean()),
        fmt(s.variance()),
        fmt(s.std()),
        fmt(s.min()),
        fmt(s.max()),
        fmt(s.median()),
        fmt(s.p05()),
        fmt(s.p95())
      ));
    });
  }

  private static String fmt(double v) {
    return Double.isNaN(v) ? "NaN" : String.format("%.6f", v);
  }

  private Task4StatsTablePrinter() {}
}
