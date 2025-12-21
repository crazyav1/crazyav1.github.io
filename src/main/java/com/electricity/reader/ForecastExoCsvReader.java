package com.electricity.reader;

import com.electricity.models.CsvIndex;
import com.electricity.models.ForecastExoRow;
import com.electricity.util.CsvParser;
import com.electricity.util.TimeParsers;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class ForecastExoCsvReader {

  public static List<ForecastExoRow> load(Path path) throws Exception {
    try (BufferedReader br = Files.newBufferedReader(path)) {
      String header = br.readLine();
      if (header == null) return List.of();

      int tsIdx = CsvIndex.indexOf(header, "timestamp");
      int dIdx  = CsvIndex.indexOf(header, "Demand");

      int tempIdx   = CsvIndex.indexOf(header, "Temperature");
      int pressIdx  = CsvIndex.indexOf(header, "Pressure (hPa)");
      int cloudIdx  = CsvIndex.indexOf(header, "Cloud_cover (%)");
      int windIdx   = CsvIndex.indexOf(header, "Wind_speed_10m (km/h)");

      var out = new ArrayList<ForecastExoRow>();
      String line;
      while ((line = br.readLine()) != null) {
        var p = line.split(",", -1);

        var ts = TimeParsers.parseOffsetDateTime(p[tsIdx]);
        double demand = CsvParser.parseDoubleOrNaN(p[dIdx]);

        double temp  = CsvParser.parseDoubleOrNaN(p[tempIdx]);
        double press = CsvParser.parseDoubleOrNaN(p[pressIdx]);
        double cloud = CsvParser.parseDoubleOrNaN(p[cloudIdx]);
        double wind  = CsvParser.parseDoubleOrNaN(p[windIdx]);

        out.add(new ForecastExoRow(ts, demand, temp, cloud, wind, press));
      }
      return out;
    }
  }

  private ForecastExoCsvReader() {}
}
