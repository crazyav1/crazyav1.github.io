package com.electricity.util;

import com.electricity.models.Task11ScheduleRow;

import java.util.List;

public final class Task11CostCalculator {

  public record CostSeries(double total, double[] hourly, double[] cumulative) {}

  /**
   * Computes hourly and cumulative cost:
   * cost[t] = price[t] * grid_import[t] - exportFactor * price[t] * grid_export[t]
   */
  public static CostSeries costFromOptimisedSchedule(List<Task11ScheduleRow> rows, double exportPriceFactor) {
    int n = rows.size();
    double[] hourly = new double[n];
    double[] cum = new double[n];

    double total = 0.0;
    for (int t = 0; t < n; t++) {
      var r = rows.get(t);
      double price = nz(r.price());
      double gi = nz(r.gridImport());
      double ge = nz(r.gridExport());

      double c = price * gi - exportPriceFactor * price * ge;
      hourly[t] = c;
      total += c;
      cum[t] = total;
    }

    return new CostSeries(total, hourly, cum);
  }

  /**
   * Baseline: "without PV based on demand"
   * Assume all demand is covered by grid import, no export, no battery.
   * cost[t] = price[t] * demand_forecast[t]
   */
  public static CostSeries costWithoutPvBaseline(List<Task11ScheduleRow> rows) {
    int n = rows.size();
    double[] hourly = new double[n];
    double[] cum = new double[n];

    double total = 0.0;
    for (int t = 0; t < n; t++) {
      var r = rows.get(t);
      double price = nz(r.price());
      double demand = nz(r.demandForecast());

      double c = price * demand;
      hourly[t] = c;
      total += c;
      cum[t] = total;
    }

    return new CostSeries(total, hourly, cum);
  }

  private static double nz(double v) {
    if (Double.isNaN(v) || Double.isInfinite(v)) return 0.0;
    return v;
  }

  /**
   * Baseline: PV available but NO battery.
   * PV is used to cover demand, remainder bought from grid.
   * No export, no storage.
   */
  public static CostSeries costPvNoBattery(List<Task11ScheduleRow> rows) {
    int n = rows.size();
    double[] hourly = new double[n];
    double[] cum = new double[n];

    double total = 0.0;
    for (int t = 0; t < n; t++) {
      var r = rows.get(t);

      double price = nz(r.price());
      double demand = nz(r.demandForecast());
      double pv = nz(r.pvForecast());

      double gridImport = Math.max(0.0, demand - pv);
      double c = price * gridImport;

      hourly[t] = c;
      total += c;
      cum[t] = total;
    }

    return new CostSeries(total, hourly, cum);
  }


  private Task11CostCalculator() {}
}
