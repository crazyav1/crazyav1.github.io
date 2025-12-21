package com.electricity.reader;

import com.electricity.models.CsvIndex;
import com.electricity.models.ForecastRow;
import com.electricity.util.CsvParser;
import com.electricity.util.TimeParsers;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ForecastCsvReader {

  public static List<ForecastRow> load(Path path) throws Exception {
    try (BufferedReader br = Files.newBufferedReader(path)) {
      String header = br.readLine();
      if (header == null) return List.of();

      int tsIdx = CsvIndex.indexOf(header, "timestamp");
      int dIdx = CsvIndex.indexOf(header, "Demand");

      var out = new ArrayList<ForecastRow>();
      String line;
      while ((line = br.readLine()) != null) {
        var parts = line.split(",", -1);
        var ts = TimeParsers.parseOffsetDateTime(parts[tsIdx]);
        var demand = CsvParser.parseDoubleOrNaN(parts[dIdx]);
        out.add(new ForecastRow(ts, demand));
      }
      return out;
    }
  }

  private ForecastCsvReader() {}
}
