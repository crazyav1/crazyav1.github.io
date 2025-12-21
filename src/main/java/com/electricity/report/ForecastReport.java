package com.electricity.report;

import com.electricity.util.Metrics;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ForecastReport {

  public record DayResult(String day, double[] actual, Map<String, double[]> preds) {
    public static DayResult of(String day, double[] actual, Map<String, double[]> preds) {
      return new DayResult(day, actual, preds);
    }
  }

  public record OverallResult(String model, double nrmse) {}

  private final List<DayResult> days;

  private ForecastReport(List<DayResult> days) { this.days = days; }

  public static ForecastReport from(List<DayResult> days) { return new ForecastReport(days); }

  public List<DayResult> days() { return days; }

  public List<OverallResult> overall() {
    var byModel = new LinkedHashMap<String, List<Double>>();
    var actualAll = new ArrayList<Double>();

    for (var d : days) {
      for (double a : d.actual()) actualAll.add(a);
      d.preds().forEach((name, p) -> {
        byModel.computeIfAbsent(name, k -> new ArrayList<>());
        for (double v : p) byModel.get(name).add(v);
      });
    }

    double[] actual = actualAll.stream().mapToDouble(Double::doubleValue).toArray();

    var out = new ArrayList<OverallResult>();
    byModel.forEach((name, list) -> {
      double[] pred = list.stream().mapToDouble(Double::doubleValue).toArray();
      double nrmse = Metrics.nrmse(actual, pred, 0, actual.length);
      out.add(new OverallResult(name, nrmse));
    });
    return out;
  }

  public Map<String, List<Double>> perDayNrmse() {
    var out = new LinkedHashMap<String, List<Double>>();
    for (var d : days) {
      d.preds().forEach((name, p) -> {
        out.computeIfAbsent(name, k -> new ArrayList<>());
        out.get(name).add(Metrics.nrmse(d.actual(), p, 0, d.actual().length));
      });
    }
    return out;
  }
}
