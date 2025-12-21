package com.electricity.exporter;

import com.electricity.report.ForecastReport;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ForecastExporter {

  public static void saveCsv(ForecastReport report, Path out) throws Exception {
    try (BufferedWriter w = Files.newBufferedWriter(out)) {
      w.write("day,hour,actual");
      var models = report.days().getFirst().preds().keySet();
      for (var m : models) w.write("," + m);
      w.newLine();

      for (var d : report.days()) {
        for (int h = 0; h < d.actual().length; h++) {
          w.write(d.day() + "," + h + "," + d.actual()[h]);
          for (var m : models) {
            w.write("," + d.preds().get(m)[h]);
          }
          w.newLine();
        }
      }
    }
  }

  private ForecastExporter() {}
}
