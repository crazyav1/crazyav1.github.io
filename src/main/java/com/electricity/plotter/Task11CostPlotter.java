package com.electricity.plotter;

import com.electricity.config.OutputConfig;
import com.electricity.exporter.ChartExporter;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

public final class Task11CostPlotter {

  private static final int W = 1100;
  private static final int H = 650;

  private static final OutputConfig OUT = new OutputConfig(Path.of("task11_figures"), W, false);

  public static void plotTotalCost(double totalNoPv, double totalPvLow, double totalPvHigh) {
    CategoryChart chart = new CategoryChartBuilder()
                            .width(W)
                            .height(H)
                            .title("Task 11: Total cost comparison (24h)")
                            .xAxisTitle("Scenario")
                            .yAxisTitle("Total cost")
                            .build();

    chart.getStyler()
      .setXAxisLabelRotation(90);
    chart.getStyler()
      .setAvailableSpaceFill(0.25);

    var categories = List.of("No PV (baseline)", "PV_low (optimised)", "PV_high (optimised)");
    var values = List.of(totalNoPv, totalPvLow, totalPvHigh);

    chart.addSeries("TotalCost", categories, values);

    ChartExporter.saveSvg(chart, OUT, "task11_total_cost");
    ChartExporter.savePng(chart, OUT, "task11_total_cost");
  }

  public static void plotCumulativeCost(    double[] cumNoPv,
                                            double[] cumPvNoBatt,
                                            double[] cumLow,
                                            double[] cumHigh) {
    XYChart chart = new XYChartBuilder()
                      .width(W)
                      .height(H)
                      .title("Task 11: Cumulative cost over 24 hours")
                      .xAxisTitle("Hour")
                      .yAxisTitle("Cumulative cost")
                      .build();

    chart.getStyler()
      .setLegendPosition(Styler.LegendPosition.OutsideE)
      .setMarkerSize(0);

    var x = IntStream.range(0, cumNoPv.length)
              .mapToDouble(i -> i)
              .boxed()
              .toList();

    chart.addSeries("No PV, no battery", x, toList(cumNoPv));
    chart.addSeries("PV only (no battery)", x, toList(cumPvNoBatt));
    chart.addSeries("PV_low + battery", x, toList(cumLow));
    chart.addSeries("PV_high + battery", x, toList(cumHigh));

    ChartExporter.saveSvg(chart, OUT, "task11_cumulative_cost");
    ChartExporter.savePng(chart, OUT, "task11_cumulative_cost");

  }

  private static List<Double> toList(double[] a) {
    return IntStream.range(0, a.length).mapToDouble(i -> a[i]).boxed().toList();
  }

  private Task11CostPlotter() {}
}
