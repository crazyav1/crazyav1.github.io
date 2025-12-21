package com.electricity.plotter;

import com.electricity.config.OutputConfig;
import com.electricity.config.Task5Config;
import com.electricity.exporter.ChartExporter;
import com.electricity.models.DataRow;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.util.Date;
import java.util.List;
import java.util.Random;

import static org.knowm.xchart.XYSeries.XYSeriesRenderStyle.Scatter;

public final class Task5Plotter {

  public static void exportDemandTimeSeries(List<DataRow> rows, Task5Config cfg, OutputConfig out) {
    if (rows.isEmpty()) {
      return;
    }

    var start = rows.getFirst().timestamp();
    var end = start.plus(cfg.timeSeriesWindow());

    var sub = rows.stream()
                .filter(r -> !r.timestamp().isBefore(start) && r.timestamp().isBefore(end))
                .filter(r -> !Double.isNaN(r.demand()))
                .toList();

    var x = sub.stream().map(r -> Date.from(r.timestamp().toInstant())).toList();
    var y = sub.stream().map(DataRow::demand).toList();

    var chart = new XYChartBuilder()
                  .width(cfg.timeSeriesWidth())
                  .height(cfg.timeSeriesHeight())
                  .title("Demand normalized time series (first window)")
                  .xAxisTitle("Time")
                  .yAxisTitle("Demand")
                  .build();

    chart.getStyler().setDatePattern("dd.MM.yyyy HH:mm");
    chart.getStyler().setLegendVisible(false);
    chart.getStyler().setMarkerSize(0);
    chart.getStyler().setPlotGridLinesVisible(true);

    chart.addSeries("Demand", x, y);

    ChartExporter.saveSvg(chart, out, "task5_timeseries_demand" + new Random().nextInt());
    ChartExporter.savePng(chart, out, "task5_timeseries_demand" + new Random().nextInt());
    if (out.showOnScreen()) new SwingWrapper<>(chart).displayChart();
  }

  public static void exportDemandHistogram(double[] demand, Task5Config cfg, OutputConfig out) {
    var values = java.util.Arrays.stream(demand).filter(v -> !Double.isNaN(v)).boxed().toList();
    var hist = new Histogram(values, cfg.histogramBins());

    var chart = new CategoryChartBuilder()
                  .width(cfg.histWidth())
                  .height(cfg.histHeight())
                  .title("Demand normalized distribution (histogram)")
                  .xAxisTitle("Demand")
                  .yAxisTitle("Count")
                  .build();

    chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);
    chart.getStyler().setXAxisLabelRotation(90);
    chart.addSeries("Demand", hist.getxAxisData(), hist.getyAxisData());

    ChartExporter.saveSvg(chart, out, "task5_histogram_demand");
    ChartExporter.savePng(chart, out, "task5_histogram_demand");
    if (out.showOnScreen()) new SwingWrapper<>(chart).displayChart();
  }

  public static void exportDemandVsTemperatureScatter(List<DataRow> rows, Task5Config cfg, OutputConfig out) {
    var pairs = rows.stream()
                  .filter(r -> !Double.isNaN(r.demand()))
                  .filter(r -> !Double.isNaN(r.temperature()) )
                  .map(r -> new double[]{r.temperature(), r.demand()})
                  .toList();

    if (pairs.isEmpty()) return;

    var x = pairs.stream().map(p -> p[0]).toList();
    var y = pairs.stream().map(p -> p[1]).toList();

    var chart = new XYChartBuilder()
                  .width(cfg.scatterWidth())
                  .height(cfg.scatterHeight())
                  .title("Demand normalized vs Temperature (scatter)")
                  .xAxisTitle("Temperature (°C)")
                  .yAxisTitle("Demand")
                  .build();

    chart.getStyler().setDefaultSeriesRenderStyle(Scatter);
    chart.getStyler().setLegendVisible(false);
    chart.getStyler().setPlotGridLinesVisible(true);

    chart.addSeries("Points", x, y);

    ChartExporter.saveSvg(chart, out, "task5_scatter_demand_vs_temperature");
    ChartExporter.savePng(chart, out, "task5_scatter_demand_vs_temperature");
    if (out.showOnScreen()) new SwingWrapper<>(chart).displayChart();
  }

  private Task5Plotter() {}
}
