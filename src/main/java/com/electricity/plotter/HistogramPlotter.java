package com.electricity.plotter;

import com.electricity.config.OutputConfig;
import com.electricity.models.DataRow;
import com.electricity.exporter.ChartExporter;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.Histogram;
import org.knowm.xchart.SwingWrapper;

import java.util.List;
import java.util.stream.Collectors;

import static com.electricity.models.HistogramMethods.STURGES;
import static com.electricity.util.HistogramUtils.computeBins;

public final class HistogramPlotter {

  private final OutputConfig.HistogramConfig cfg;
  private final OutputConfig out;

  public HistogramPlotter(OutputConfig.HistogramConfig cfg, OutputConfig out) {
    this.cfg = cfg;
    this.out = out;
  }

  public void plotPriceHistogram(List<DataRow> rows) {
    if (rows == null || rows.isEmpty()) {
      return;
    }

    var prices = rows.stream()
                   .map(DataRow::price)
                   .filter(p -> !Double.isNaN(p))
                   .collect(Collectors.toList());

    var bins = computeBins(prices, STURGES);
    var histogram = new Histogram(prices, bins);

    var chart = chart();
    chart.addSeries(cfg.xTitle(), histogram.getxAxisData(), histogram.getyAxisData());
    chart.getStyler().setXAxisLabelRotation(cfg.xLabelRotationDeg());

    ChartExporter.saveSvg(chart, out, "task3_histogram_price");
    ChartExporter.savePng(chart, out, "task3_histogram_price");

    if (out.showOnScreen()) {
      new SwingWrapper<>(chart).displayChart();
    }
  }

  private CategoryChart chart() {
    return new CategoryChartBuilder()
             .width(cfg.width())
             .height(cfg.height())
             .title("Price distribution")
             .xAxisTitle(cfg.xTitle())
             .yAxisTitle(cfg.yTitle())
             .build();
  }
}
