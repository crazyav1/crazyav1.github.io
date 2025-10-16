package com.electricity.viz;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Hour;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import tech.tablesaw.api.Table;

import javax.swing.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class Charts {
    public static void plotDemandPvPrice(Table df, String startIso, String endIso) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");

        TimeSeries demand = new TimeSeries("Demand [MW]");
        TimeSeries pv = new TimeSeries("PV [MW]");
        TimeSeries price = new TimeSeries("Price [€/MWh]");

        df.stream()
                .filter(row -> {
                    String ts = row.getString("timestamp");
                    return ts.compareTo(startIso) >= 0 && ts.compareTo(endIso) <= 0;
                })
                .forEach(row -> {
                    ZonedDateTime zdt = ZonedDateTime.parse(row.getString("timestamp"), fmt);
                    Date date = Date.from(zdt.toInstant());
                    demand.addOrUpdate(new Hour(date), row.getNumber("demand"));
                    pv.addOrUpdate(new Hour(date), row.getNumber("PV"));
                    price.addOrUpdate(new Hour(date), row.getNumber("price"));
                });

        var dataset = new TimeSeriesCollection();
        dataset.addSeries(demand);
        dataset.addSeries(pv);
        dataset.addSeries(price);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Demand, PV, Price", "Time", "Value", dataset, true, true, false);

        JFrame frame = new JFrame("Overview");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(new ChartPanel(chart));
        frame.pack();
        frame.setVisible(true);
    }

}
