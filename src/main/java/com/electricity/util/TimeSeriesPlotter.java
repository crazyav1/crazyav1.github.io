package com.electricity.util;

import com.electricity.models.DataRow;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.VectorGraphicsEncoder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.knowm.xchart.VectorGraphicsEncoder.VectorGraphicsFormat.SVG;
import static org.knowm.xchart.style.Styler.YAxisPosition.Right;

public class TimeSeriesPlotter {
  private static final int CHART_WIDTH = 1000;
  private static final int CHART_HEIGHT = 600;
  public static final int DAYS = 7;

  public static void plotDemandAndPv(List<DataRow> rows) {
    if (rows == null || rows.isEmpty()) {
      return;
    }

    var subset = filterDataForFirstDays(rows, DAYS);
    var times = extractTimestamps(subset);
    var demand = extractDemand(subset);
    var pv = extractPv(subset);
    var prices = extractPrices(subset);

    var chart = createChart(DAYS);
    chart.addSeries("Demand: kW", times, demand);
    chart.addSeries("PV Generation: kW", times, pv);
    chart.addSeries("Price: Euro/kWh", times, prices)
      .setYAxisGroup(1)
      .setFillColor(Color.RED);
    styleTimeSeries(chart);
    chart.setYAxisGroupTitle(1, "Price: Euro/kWh");
    chart.getStyler().setYAxisGroupPosition(1, Right);
    saveSvg(chart, Path.of("demand-pv.svg"));
    new SwingWrapper<>(chart).displayChart();
  }

  public static void saveSvg(XYChart chart, Path outputFile) {
    try {
      VectorGraphicsEncoder.saveVectorGraphic(chart, outputFile.toString(), SVG);
    } catch (IOException e) {
      System.out.println("Error saving SVG: " + e.getMessage());
    }
  }

  private static List<Double> extractPrices(List<DataRow> rows) {
    return rows.stream()
             .map(DataRow::price)
             .filter(p -> !Double.isNaN(p))
             .collect(Collectors.toList());
  }

  private static void styleTimeSeries(XYChart chart) {
    chart.getStyler()
      .setDatePattern("dd.MM.yyyy")
      .setXAxisLabelRotation(90)
      .setMarkerSize(0);
  }

  private static XYChart createChart(int days) {
    return new XYChartBuilder()
        .width(CHART_WIDTH)
        .height(CHART_HEIGHT)
        .title("Prices, Demand and PV (first %d days)".formatted(days))
        .xAxisTitle("Time")
        .yAxisTitle("Power (kW)")
        .build();
  }

  private static List<Double> extractPv(List<DataRow> subset) {
    return subset.stream().map(DataRow::pv).toList();
  }

  private static List<Double> extractDemand(List<DataRow> subset) {
    return subset.stream().map(DataRow::demand).toList();
  }

  private static List<Date> extractTimestamps(List<DataRow> subset) {
    return subset.stream()
             .map(r -> Date.from(r.timestamp().toInstant()))
             .toList();
  }

  private static List<DataRow> filterDataForFirstDays(List<DataRow> rows, int days) {
    OffsetDateTime start = rows.get(0).timestamp();
    OffsetDateTime end = start.plusDays(days);

    return rows.stream()
        .filter(r -> !r.timestamp().isBefore(start) && r.timestamp().isBefore(end))
        .toList();
  }
}
