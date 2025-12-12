package com.electricity;

import com.electricity.models.DataRow;
import com.electricity.util.CsvReader;
import com.electricity.util.HistogramPlotter;
import com.electricity.util.StatsUtils;
import com.electricity.util.TimeSeriesPlotter;

import java.nio.file.Path;
import java.util.List;

public class Main {

  static void main(String[] args) throws Exception {
    Path csvPath = Path.of("./src/main/resources/train_211628.csv");
    List<DataRow> rows = CsvReader.loadTrainTest(csvPath);

    System.out.println("Total rows: " + rows.size());
    System.out.println("First 3 rows:");
    rows.stream().limit(3).forEach(System.out::println);

    System.out.println("Last 3 rows:");
    rows.stream().skip(Math.max(0, rows.size() - 3)).forEach(System.out::println);

    System.out.println("\n=== Basic stats ===");
    StatsUtils.printBasicStats(rows);

    TimeSeriesPlotter.plotDemandAndPv(rows);
    HistogramPlotter.plotPriceHistogram(rows);
  }

}
