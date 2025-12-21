package com.electricity.plotter;

import com.electricity.config.OutputConfig;
import com.electricity.exporter.ChartExporter;
import com.electricity.models.DataRow;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChartBuilder;

import java.util.List;

import static org.knowm.xchart.XYSeries.XYSeriesRenderStyle.Scatter;

public final class ScatterPlotter {

  private final OutputConfig out;
  private final ScatterConfig cfg;

  public ScatterPlotter(ScatterConfig cfg, OutputConfig out) {
    this.cfg = cfg;
    this.out = out;
  }

  public void plotDemandVsTemperature(List<DataRow> rows) {
    if (rows == null || rows.isEmpty()) {
      return;
    }

    var pairs = rows.stream()
                  .filter(r -> !Double.isNaN(r.temperature()) && !Double.isNaN(r.demand()))
                  .map(r -> new double[]{r.temperature(), r.demand()})
                  .toList();

    var x = pairs.stream().map(p -> p[0]).toList();
    var y = pairs.stream().map(p -> p[1]).toList();

    var chart = new XYChartBuilder()
                  .width(cfg.width())
                  .height(cfg.height())
                  .title("Demand vs Temperature")
                  .xAxisTitle("Temperature (°C)")
                  .yAxisTitle("Demand (kW)")
                  .build();

    chart.getStyler().setDefaultSeriesRenderStyle(Scatter);
    chart.getStyler().setLegendVisible(false);
    chart.getStyler().setMarkerSize(cfg.markerSize());

    chart.addSeries("Demand vs Temperature", x, y);

    ChartExporter.saveSvg(chart, out, "scatter_demand_vs_temperature");
    ChartExporter.savePng(chart, out, "scatter_demand_vs_temperature");

    if (out.showOnScreen()) {
      new SwingWrapper<>(chart).displayChart();
    }
  }

  public record ScatterConfig(int width, int height, int markerSize) {
    public static ScatterConfig defaults() {
      return new ScatterConfig(900, 600, 4);
    }
  }
}
