package com.electricity.plotter;

import com.electricity.config.OutputConfig;
import com.electricity.models.DataRow;
import com.electricity.exporter.ChartExporter;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChartBuilder;

import java.util.Date;
import java.util.List;

public final class PvBeforeAfterPlotter {

  private final OutputConfig out;

  public PvBeforeAfterPlotter(OutputConfig out) {
    this.out = out;
  }

  public void plot(List<DataRow> rows, double[] original, double[] uni, double[] multi,
                   int startIndex, int lengthHours) {
    var end = Math.min(rows.size(), startIndex + lengthHours);

    var t = rows.subList(startIndex, end).stream()
              .map(r -> Date.from(r.timestamp().toInstant()))
              .toList();

    var o = slice(original, startIndex, end);
    var u = slice(uni, startIndex, end);
    var m = slice(multi, startIndex, end);

    var chart = new XYChartBuilder()
                  .width(1100).height(600)
                  .title("PV_mod1 before and after imputation")
                  .xAxisTitle("Time")
                  .yAxisTitle("PV_mod1 (kW)")
                  .build();

    chart.getStyler().setDatePattern("dd.MM.yyyy HH:mm");
    chart.getStyler().setMarkerSize(0);
    chart.getStyler().setPlotGridLinesVisible(true);

    chart.addSeries("Original PV_mod1", t, o);
    chart.addSeries("Univariate (linear)", t, u);
    chart.addSeries("Multivariate (OLS)", t, m);

    ChartExporter.saveSvg(chart, out, "task4_pv_mod1_before_after");
    ChartExporter.savePng(chart, out, "task4_pv_mod1_before_after");

    if (out.showOnScreen()) new SwingWrapper<>(chart).displayChart();
  }

  private static List<Double> slice(double[] arr, int start, int end) {
    var out = new java.util.ArrayList<Double>(end - start);
    for (int i = start; i < end; i++) {
      out.add(arr[i]);
    }
    return out;
  }
}
