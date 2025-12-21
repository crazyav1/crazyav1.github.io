package com.electricity.plotter;

import com.electricity.config.OutputConfig;
import com.electricity.exporter.ChartExporter;
import com.electricity.util.TypicalProfiles;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public final class TypicalProfilesPlotter {

  public static void plot(Map<String, TypicalProfiles.Profile> profiles, OutputConfig out) {
    List<Integer> hours = IntStream.range(0, 24).boxed().toList();

    XYChart chart = new XYChartBuilder()
                      .width(900)
                      .height(600)
                      .title("Typical demand profiles")
                      .xAxisTitle("Hour of day")
                      .yAxisTitle("Mean demand (kW)")
                      .build();

    profiles.forEach((name, p) -> {
      var y = Arrays.stream(p.hourlyMean()).boxed().toList();
      chart.addSeries(name, hours, y);
    });

    ChartExporter.saveSvg(chart, out, "task6_typical_profiles");
    ChartExporter.savePng(chart, out, "task6_typical_profiles");

    if (out.showOnScreen()) new SwingWrapper<>(chart).displayChart();
  }

  private TypicalProfilesPlotter() {}
}
