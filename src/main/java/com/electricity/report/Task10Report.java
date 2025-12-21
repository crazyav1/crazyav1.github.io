package com.electricity.report;

import com.electricity.util.Metrics;

import java.util.*;

public final class Task10Report {

  public record DayResult(String day, double[] actual, Map<String, double[]> preds) {
    public static DayResult of(String day, double[] actual, Map<String, double[]> preds) {
      return new DayResult(day, actual, preds);
    }
  }

  public record Overall(String model, double mae, double nrmse) {}

  private final List<DayResult> days;

  private Task10Report(List<DayResult> days) { this.days = days; }

  public static Task10Report from(List<DayResult> days) { return new Task10Report(days); }

  public List<Overall> overall() {
    var actualAll = new ArrayList<Double>();
    var predAll = new LinkedHashMap<String, ArrayList<Double>>();

    for (var d : days) {
      for (double a : d.actual()) actualAll.add(a);
      for (var e : d.preds().entrySet()) {
        predAll.computeIfAbsent(e.getKey(), k -> new ArrayList<>());
        for (double v : e.getValue()) predAll.get(e.getKey()).add(v);
      }
    }

    double[] actual = actualAll.stream().mapToDouble(Double::doubleValue).toArray();

    var out = new ArrayList<Overall>();
    for (var e : predAll.entrySet()) {
      double[] pred = e.getValue().stream().mapToDouble(Double::doubleValue).toArray();
      out.add(new Overall(e.getKey(), Metrics.mae(actual, pred), Metrics.nrmse(actual, pred,0, actual.length)));
    }
    return out;
  }

  public Map<String, Overall> overallMap() {
    var map = new LinkedHashMap<String, Overall>();
    for (var o : overall()) map.put(o.model(), o);
    return map;
  }

  private static double pctImprovement(double base, double improved) {
    if (Double.isNaN(base) || base == 0.0) {
      return Double.NaN;
    }
    return (base - improved) / base * 100.0;
  }

  public Improvement improvement(String baseline, String improved) {
    var m = overallMap();
    var b = m.get(baseline);
    var i = m.get(improved);
    if (b == null || i == null) throw new IllegalArgumentException("Missing models in report");

    return new Improvement(
      baseline,
      improved,
      pctImprovement(b.mae(), i.mae()),
      pctImprovement(b.nrmse(), i.nrmse())
    );
  }

  public record Improvement(String baseline, String improved, double maeImprovementPct, double nrmseImprovementPct) {}
}
