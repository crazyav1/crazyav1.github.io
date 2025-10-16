package com.electricity;

import org.apache.batik.svggen.SVGGraphics2D;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import tech.tablesaw.api.Table;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.apache.batik.dom.GenericDOMImplementation.getDOMImplementation;

public class Main2 {
    public static void main(String[] args) throws IOException {
        var df = Table.read().csv("data/forecast.csv");
        var timeStrings = df.stringColumn("timestamp").asList();
        var demandValues = df.numberColumn("demand").asList();
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");

        var series = createTimeSeries(timeStrings, formatter, demandValues);

        var dataset = new TimeSeriesCollection(series);

        var chart = ChartFactory.createTimeSeriesChart(
                "Hourly Electricity Demand",
                "Time",
                "Demand [MW]",
                dataset,
                true,
                true,
                false
        );

        stylePlot(chart);

        createChartFile(chart);

        createCharWindow(chart);
    }

    private static TimeSeries createTimeSeries(java.util.List<String> timeStrings, DateTimeFormatter formatter, java.util.List<? extends Number> demandValues) {
        var series = new TimeSeries("Demand (MW)");
        for (int i = 0; i < timeStrings.size(); i++) {
            var zdt = ZonedDateTime.parse(timeStrings.get(i), formatter);
            var minute = new Minute(Date.from(zdt.toInstant()));
            series.add(minute, demandValues.get(i));
        }
        return series;
    }

    private static void stylePlot(JFreeChart chart) {
        var plot = chart.getXYPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(Color.GRAY);
        plot.setRangeGridlinePaint(Color.GRAY);

        chart.getTitle().setFont(new Font("Arial", Font.BOLD, 16));
        plot.getDomainAxis().setLabelFont(new Font("Arial", Font.PLAIN, 14));
        plot.getRangeAxis().setLabelFont(new Font("Arial", Font.PLAIN, 14));

        plot.setRenderer(createRenderer());
    }

    private static XYLineAndShapeRenderer createRenderer() {
        var renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(248, 2, 2));
        renderer.setSeriesStroke(0, new BasicStroke(2.5f));
        renderer.setSeriesShapesVisible(0, false);
        return renderer;
    }

    private static void createCharWindow(JFreeChart chart) {
        var frame = new JFrame("Electricity Demand");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }

    private static void createChartFile(JFreeChart chart) throws IOException {
        var document = getDOMImplementation().createDocument(null, "svg", null);
        var svgGenerator = new SVGGraphics2D(document);
        svgGenerator.setSVGCanvasSize(new Dimension(1000, 400));

        chart.draw(svgGenerator, new Rectangle(900, 300));

        try (FileWriter out = new FileWriter("chart.svg")) {
            svgGenerator.stream(out, true);
        }
    }
}
