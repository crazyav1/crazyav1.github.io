package com.electricity.util;

import com.electricity.models.DataRow;
import org.knowm.xchart.Histogram;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;

import java.util.List;
import java.util.stream.Collectors;

import static com.electricity.util.HistogramMethods.STURGES;
import static com.electricity.util.HistogramUtils.computeBins;

public class HistogramPlotter {

  public static final int NUM_BINS = 30;

  public static void plotPriceHistogram(List<DataRow> rows) {
    var prices = rows.stream()
                            .map(DataRow::price)
                            .filter(p -> !Double.isNaN(p))
                            .collect(Collectors.toList());

    var histogram = new Histogram (prices, computeBins(prices, STURGES));

    var chart = new CategoryChartBuilder()
                            .width(800)
                            .height(600)
                            .title("Price distribution")
                            .xAxisTitle("Price: Euro/kWh")
                            .yAxisTitle("Count")
                            .build();

    chart.addSeries("Price: Euro/kWh", histogram.getxAxisData(), histogram.getyAxisData());
    chart.getStyler().setXAxisLabelRotation(90);
    new SwingWrapper<>(chart).displayChart();
  }
}
