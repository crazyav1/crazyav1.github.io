package com.electricity.viz;


import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import tech.tablesaw.api.Table;

public class Summaries {
    public static void describe(Table df, String col) {
        DescriptiveStatistics stats = new DescriptiveStatistics();
        df.numberColumn(col).asList().forEach(n -> stats.addValue(n.doubleValue()));
        System.out.printf("%s -> mean=%.3f, std=%.3f, min=%.3f, max=%.3f%n",
                col, stats.getMean(), stats.getStandardDeviation(), stats.getMin(), stats.getMax());
    }
}