package com.electricity.plotter;

import com.electricity.config.OutputConfig;
import com.electricity.exporter.ChartExporter;
import com.electricity.models.Task11ScheduleRow;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

import java.nio.file.Path;
import java.util.Date;
import java.util.List;


public final class Task11Plotter {

  private static final int W = 1100;
  private static final int H = 650;
  private static final OutputConfig OUT =
   new OutputConfig(Path.of("figures/task11_figures"), W, true);

  public static void plotAll(
    List<Task11ScheduleRow> low,
    List<Task11ScheduleRow> high,
    Path outDir
  ) {
    outDir.toFile().mkdirs();

    save(showSoc(low, high), "task11_soc");
    save(showGrid(low, high), "task11_grid");
    save(showBattery(low, high), "task11_battery");
    save(showDemandPv(low, high), "task11_demand_pv");
    save(showPrice(low, high), "task11_price");

  }

  private static XYChart showSoc(List<Task11ScheduleRow> low, List<Task11ScheduleRow> high) {
    var chart = base("Battery SOC (kWh)", "Time", "SOC (kWh)");
    chart.addSeries("PV_low SOC", time(low), soc(low));
    chart.addSeries("PV_high SOC", time(high), soc(high));
    styleTime(chart);
    return chart;
  }

  private static XYChart showGrid(List<Task11ScheduleRow> low, List<Task11ScheduleRow> high) {
    var chart = base("Grid power (kW)", "Time", "Power (kW)");

    chart.addSeries("PV_low Grid Import", time(low), gridIn(low));
    chart.addSeries("PV_low Grid Export (neg)", time(low), gridOutNeg(low));

    chart.addSeries("PV_high Grid Import", time(high), gridIn(high));
    chart.addSeries("PV_high Grid Export (neg)", time(high), gridOutNeg(high));

    styleTime(chart);
    return chart;
  }

  private static XYChart showBattery(List<Task11ScheduleRow> low, List<Task11ScheduleRow> high) {
    var chart = base("Battery control (kW)", "Time", "Power (kW)");
    chart.addSeries("PV_low Charge", time(low), charge(low));
    chart.addSeries("PV_low Discharge (neg)", time(low), dischargeNeg(low));

    chart.addSeries("PV_high Charge", time(high), charge(high));
    chart.addSeries("PV_high Discharge (neg)", time(high), dischargeNeg(high));

    styleTime(chart);
    return chart;
  }

  private static XYChart showDemandPv(List<Task11ScheduleRow> low, List<Task11ScheduleRow> high) {
    var chart = base("Demand vs PV usage (kW)", "Time", "Power (kW)");

    chart.addSeries("PV_low Demand forecast", time(low), demand(low));
    chart.addSeries("PV_low PV used", time(low), pvUsed(low));

    chart.addSeries("PV_high Demand forecast", time(high), demand(high));
    chart.addSeries("PV_high PV used", time(high), pvUsed(high));

    styleTime(chart);
    return chart;
  }

  private static XYChart showPrice(List<Task11ScheduleRow> low, List<Task11ScheduleRow> high) {
    var chart = base("Electricity price (EUR/kWh)", "Time", "Price (EUR/kWh)");
    chart.addSeries("PV_low Price", time(low), price(low));
    chart.addSeries("PV_high Price", time(high), price(high));
    styleTime(chart);
    return chart;
  }

  private static void save(XYChart chart, String name) {
    ChartExporter.saveSvg(chart, OUT, name);
    ChartExporter.savePng(chart, OUT, name);
  }

  private static XYChart base(String title, String x, String y) {
    var c = new XYChartBuilder()
              .width(W)
              .height(H)
              .title(title)
              .xAxisTitle(x)
              .yAxisTitle(y)
              .build();

    c.getStyler()
      .setLegendPosition(Styler.LegendPosition.OutsideE)
      .setMarkerSize(0);
    c.getStyler().setXAxisLabelRotation(90);
    return c;
  }

  private static void styleTime(XYChart chart) {
    chart.getStyler().setDatePattern("dd.MM HH:mm");
  }

  private static List<Date> time(List<Task11ScheduleRow> r) {
    return r.stream().map(x -> Date.from(x.timestamp().toInstant())).toList();
  }

  private static List<Double> soc(List<Task11ScheduleRow> r) { return r.stream().map(Task11ScheduleRow::soc).toList(); }
  private static List<Double> charge(List<Task11ScheduleRow> r) { return r.stream().map(Task11ScheduleRow::charge).toList(); }
  private static List<Double> dischargeNeg(List<Task11ScheduleRow> r) { return r.stream().map(x -> -x.discharge()).toList(); }

  private static List<Double> gridIn(List<Task11ScheduleRow> r) { return r.stream().map(Task11ScheduleRow::gridImport).toList(); }
  private static List<Double> gridOutNeg(List<Task11ScheduleRow> r) { return r.stream().map(x -> -x.gridExport()).toList(); }

  private static List<Double> demand(List<Task11ScheduleRow> r) { return r.stream().map(Task11ScheduleRow::demandForecast).toList(); }
  private static List<Double> pvUsed(List<Task11ScheduleRow> r) { return r.stream().map(Task11ScheduleRow::pvUsed).toList(); }

  private static List<Double> price(List<Task11ScheduleRow> r) { return r.stream().map(Task11ScheduleRow::price).toList(); }

  private Task11Plotter() {}
}
