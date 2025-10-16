package com.electricity;

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


public class Main3 {
    public static void main(String[] args) throws Exception {
        Table train = Table.read().csv("data/train_211628.csv");
        Table forecast = Table.read().csv("data/forecast.csv");
        Table opt = Table.read().csv("data/optimisation.csv");

        System.out.println(train.structure());
        System.out.println(train.first(5));
    }

}
