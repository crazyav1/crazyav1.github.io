package com.electricity.plotter;

import com.electricity.config.OutputConfig;
import com.electricity.exporter.ChartExporter;
import com.electricity.models.AlignedPoints;
import com.electricity.models.DataRow;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.knowm.xchart.style.Styler.YAxisPosition.Right;

public final class TimeSeriesPlotter {

  private final OutputConfig.TimeSeriesConfig cfg;
  private final OutputConfig out;

  public TimeSeriesPlotter(OutputConfig.TimeSeriesConfig cfg, OutputConfig out) {
    this.cfg = cfg;
    this.out = out;
  }

  public void plotDemandPvPriceFirstDays(List<DataRow> rows) {
    if (rows == null || rows.isEmpty()) {
      return;
    }

    var window = firstDays(rows, cfg.days());
    var points = align(window);

    var chart = chart("Prices, Demand and PV - first %d days".formatted(cfg.days()));
    chart.addSeries("Demand (kW)", points.time(), points.demand());
    chart.addSeries("PV (kW)", points.time(), points.pv());

    chart.addSeries("Price (Euro/kWh)", points.time(), points.price())
      .setYAxisGroup(1)
      .setFillColor(Color.RED);

    style(chart);
    chart.setYAxisGroupTitle(1, "Price (Euro/kWh)");
    chart.getStyler().setYAxisGroupPosition(1, Right);

    ChartExporter.saveSvg(chart, out, "task3_timeseries_demand_pv_price");
    ChartExporter.savePng(chart, out, "task3_timeseries_demand_pv_price");

    if (out.showOnScreen()) {
      new SwingWrapper<>(chart).displayChart();
    }
  }

  private XYChart chart(String title) {
    return new XYChartBuilder()
             .width(cfg.width())
             .height(cfg.height())
             .title(title)
             .xAxisTitle("Time")
             .yAxisTitle("Power (kW)")
             .build();
  }

  private void style(XYChart chart) {
    chart.getStyler()
      .setDatePattern(cfg.datePattern())
      .setXAxisLabelRotation(cfg.xLabelRotationDeg())
      .setMarkerSize(0);
  }

  private static List<DataRow> firstDays(List<DataRow> rows, int days) {
    var start = rows.getFirst().timestamp();
    var end = start.plusDays(days);
    return rows.stream()
             .filter(r -> !r.timestamp().isBefore(start) && r.timestamp().isBefore(end))
             .toList();
  }

  public static AlignedPoints align(List<DataRow> subset) {
    var tPow = new ArrayList<Date>(subset.size());
    var demand = new ArrayList<Double>(subset.size());
    var pv = new ArrayList<Double>(subset.size());
    var price = new ArrayList<Double>(subset.size());

    subset.forEach(r -> {
      var ts = Date.from(r.timestamp().toInstant());
        tPow.add(ts);
        demand.add(r.demand());
        pv.add(r.pv());
        price.add(r.price());
    });

    return new AlignedPoints(tPow, demand, pv, price);
  }
}
