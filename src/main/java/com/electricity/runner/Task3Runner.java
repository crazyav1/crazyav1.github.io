package com.electricity.runner;

import com.electricity.config.OutputConfig;
import com.electricity.plotter.HistogramPlotter;
import com.electricity.plotter.ScatterPlotter;
import com.electricity.plotter.TimeSeriesPlotter;
import com.electricity.reader.CsvReader;
import com.electricity.service.StatsService;

import java.nio.file.Path;

import static com.electricity.config.Constants.TRAIN_PATH;

public final class Task3Runner {

  private static final OutputConfig OUT = OutputConfig.defaults(Path.of("figures"));

  static void main() throws Exception {
    var rows = CsvReader.loadTrainTest(TRAIN_PATH);
    var summaries = StatsService.summarizeCore(rows);
    StatsService.printReport(summaries);

    var ts = new TimeSeriesPlotter(OutputConfig.TimeSeriesConfig.defaults(), OUT);
    ts.plotDemandPvPriceFirstDays(rows);

    var hist = new HistogramPlotter(OutputConfig.HistogramConfig.defaults(), OUT);
    hist.plotPriceHistogram(rows);

    var scatter = new ScatterPlotter(
      ScatterPlotter.ScatterConfig.defaults(),
      OUT
    );
    scatter.plotDemandVsTemperature(rows);

    System.out.println("Figures saved to: " + OUT.dir().toAbsolutePath());
  }

  private Task3Runner() {
  }
}
