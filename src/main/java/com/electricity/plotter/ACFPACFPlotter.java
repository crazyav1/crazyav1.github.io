package com.electricity.plotter;

import com.electricity.config.OutputConfig;
import com.electricity.exporter.ChartExporter;
import org.knowm.xchart.CategoryChartBuilder;

import java.util.List;
import java.util.stream.IntStream;

public final class ACFPACFPlotter {

  public static void plot(double[] acf, double[] pacf, OutputConfig out) {
    var lags = IntStream.range(0, acf.length).boxed().toList();

    var acfChart = new CategoryChartBuilder()
                               .width(900).height(600)
                               .title("ACF (stationary demand)")
                               .xAxisTitle("Lag").yAxisTitle("ACF")
                               .build();

    acfChart.addSeries("ACF", lags, toList(acf));
    acfChart.getStyler().setXAxisLabelRotation(90);
    ChartExporter.saveSvg(acfChart, out, "task7_acf");
    ChartExporter.savePng(acfChart, out, "task7_acf");
    var pacfChart = new CategoryChartBuilder()
                                .width(900).height(600)
                                .title("PACF (stationary demand)")
                                .xAxisTitle("Lag").yAxisTitle("PACF")
                                .build();

    pacfChart.addSeries("PACF", lags, toList(pacf));
    pacfChart.getStyler().setXAxisLabelRotation(90);
    ChartExporter.saveSvg(pacfChart, out, "task7_pacf");
    ChartExporter.savePng(pacfChart, out, "task7_pacf");
  }

  private static List<Double> toList(double[] x) {
    return java.util.Arrays.stream(x).boxed().toList();
  }

  private ACFPACFPlotter() {}
}
