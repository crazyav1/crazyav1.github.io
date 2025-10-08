package com.electricity;

import org.knowm.xchart.*;
import org.knowm.xchart.style.markers.SeriesMarkers;
import tech.tablesaw.api.Table;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.knowm.xchart.VectorGraphicsEncoder.saveVectorGraphic;
import static org.knowm.xchart.style.Styler.ChartTheme.Matlab;


public class Main {
    static void main() throws IOException {
//        // Load CSV with Tablesaw
//        Table df = Table.read().csv("data/sample.csv");
//        System.out.println(df.summary());
//
//        // Basic stats with Apache Commons Math
//        double[] values = {1, 2, 3, 4, 5};
//        DescriptiveStatistics stats = new DescriptiveStatistics();
//        for (double v : values) {
//            stats.addValue(v);
//        }
//        System.out.println("Mean: " + stats.getMean());
//        System.out.println("Std Dev: " + stats.getStandardDeviation());
//
//        // Plot with XChart
//        double[] x = {1, 2, 3, 4};
//        double[] y = {10, 20, 25, 30};
//        XYChart chart = new XYChartBuilder().title("Line Plot").width(600).height(400).build();
//        chart.addSeries("y", x, y);
//        new SwingWrapper<>(chart).displayChart();

        var df = Table.read().csv("data/forecast.csv");
        var timeStrings = df.stringColumn("timestamp").asList();
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");

        var time = timeStrings.stream()
                .map(s -> LocalDateTime.parse(s, formatter))
                .map(ldt -> Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()))
                .collect(Collectors.toList());
        var demand = df.numberColumn("demand").asList();

        var chart = createChart();

        addSeries(chart, time, demand);

        saveVectorGraphic(chart, "./demand-chart", VectorGraphicsEncoder.VectorGraphicsFormat.SVG);

        new SwingWrapper<>(chart).displayChart();
    }

    private static void addSeries(XYChart chart, List<Date> time, List<? extends Number> demand) {
        var series = chart.addSeries("Demand (MW)", time, demand);
        series.setMarker(SeriesMarkers.NONE);
    }

    private static XYChart createChart() {
        return new XYChartBuilder()
                .width(1000)
                .height(400)
                .theme(Matlab)
                .title("Hourly Electricity Demand")
                .xAxisTitle("Time")
                .yAxisTitle("Demand [MW]")
                .build();
    }

}
