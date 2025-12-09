package com.electricity.util;

import com.electricity.models.DataRow;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.function.ToDoubleFunction;

public class StatsUtils {

  public static void printColumnStats(String name, List<DataRow> rows,
                                      ToDoubleFunction<DataRow> extractor) {
    DoubleSummaryStatistics stats = rows.stream()
                                      .mapToDouble(extractor)
                                      .filter(d -> !Double.isNaN(d))
                                      .summaryStatistics();

    long nanCount = rows.stream()
                      .mapToDouble(extractor)
                      .filter(Double::isNaN)
                      .count();

    System.out.printf(
      "%s: count=%d, min=%.4f, max=%.4f, mean=%.4f, NaNs=%d%n",
      name, stats.getCount(), stats.getMin(), stats.getMax(),
      stats.getAverage(), nanCount
    );
  }

  public static void printBasicStats(List<DataRow> rows) {
    printColumnStats("Demand", rows, DataRow::demand);
    printColumnStats("PV", rows, DataRow::pv);
    printColumnStats("Price", rows, DataRow::price);
    printColumnStats("Temperature", rows, DataRow::temperature);
    printColumnStats("WindSpeed10m", rows, DataRow::windSpeed10m);
    printColumnStats("DayMax", rows, DataRow::dayMax);
  }
}