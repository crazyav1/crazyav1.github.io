package com.electricity.plotter;

import com.electricity.config.OutputConfig;
import com.electricity.models.DataRow;
import com.electricity.exporter.ChartExporter;
import com.electricity.util.ClassicalDecomposition;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.util.Date;
import java.util.List;

public final class DecompositionPlotter {

  public static void plot(ClassicalDecomposition.Result dec, List<DataRow> rows, OutputConfig out) {
    var x = rows.stream().map(r -> Date.from(r.timestamp().toInstant())).toList();

    int start = (x.size() / 2) - 36;
    int end = (x.size() / 2) + 36;

    var x24 = x.subList(start, end);
    var observed24 = java.util.Arrays.copyOfRange(dec.observed(), start, end);
    var trend24 = java.util.Arrays.copyOfRange(dec.trend(), start, end);
    var seasonal24 = java.util.Arrays.copyOfRange(dec.seasonal(), start, end);
    var residual24 = java.util.Arrays.copyOfRange(dec.residual(), start, end);

    saveSeries("task6_observed_demand", "Observed demand", "Demand (kW)", x24, observed24, out);
    saveSeries("task6_trend", "Trend component (Tt)", "Trend", x24, trend24, out);
    saveSeries("task6_seasonal", "Seasonal component (St)", "Seasonality", x24, seasonal24, out);
    saveSeries("task6_residual", "Residual component (Rt)", "Residual", x24, residual24, out);
  }

  private static void saveSeries(
    String fileBase,
    String title,
    String yTitle,
    List<Date> x,
    double[] yArr,
    OutputConfig out
  ) {
    var y = java.util.Arrays.stream(yArr).boxed().toList();

    XYChart chart = new XYChartBuilder()
                      .width(1100)
                      .height(650)
                      .title(title)
                      .xAxisTitle("Time")
                      .yAxisTitle(yTitle)
                      .build();

    chart.getStyler().setDatePattern("dd.MM.yyyy");
    chart.getStyler().setMarkerSize(0);

    chart.addSeries(yTitle, x, y);

    ChartExporter.saveSvg(chart, out, fileBase);
    ChartExporter.savePng(chart, out, fileBase);

    if (out.showOnScreen()) {
      new SwingWrapper<>(chart).displayChart();
    }
  }

  private DecompositionPlotter() {}
}
