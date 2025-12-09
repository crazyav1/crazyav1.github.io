package com.electricity.util;

import com.electricity.models.DataRow;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

  // 2013-07-01 00:00:00+00:00
  private static final DateTimeFormatter TS_FMT =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");

  public static List<DataRow> loadTrainTest(Path csvPath) throws IOException {
    List<DataRow> rows = new ArrayList<>();

    try (BufferedReader br = Files.newBufferedReader(csvPath)) {
      // read & ignore header
      String header = br.readLine();
      if (header == null) {
        throw new IllegalStateException("Empty CSV file: " + csvPath);
      }

      String line;
      while ((line = br.readLine()) != null) {
        // keep empty columns (very important!)
        String[] cols = line.split(",", -1);

        // Basic defensive check
        if (cols.length < 19) {
          throw new IllegalStateException(
            "Expected at least 19 columns, got " + cols.length + " in line: " + line
          );
        }

        // 0: timestamp
        OffsetDateTime ts = OffsetDateTime.parse(cols[0].trim(), TS_FMT);

        // 1–3: pv_mod1..3
        double pvMod1 = parseDoubleOrNa(cols[1]);
        double pvMod2 = parseDoubleOrNa(cols[2]);
        double pvMod3 = parseDoubleOrNa(cols[3]);

        // 4: Demand
        double demand = parseDoubleOrNa(cols[4]);

        // 5: pv
        double pv = parseDoubleOrNa(cols[5]);

        // 6: Price
        double price = parseDoubleOrNa(cols[6]);

        // 7: Temperature
        double temp = parseDoubleOrNa(cols[7]);

        // 8: Pressure (hPa)
        double pressure = parseDoubleOrNa(cols[8]);

        // 9–12: cloud cover %
        double cloudCover = parseDoubleOrNa(cols[9]);
        double cloudCoverLow = parseDoubleOrNa(cols[10]);
        double cloudCoverMid = parseDoubleOrNa(cols[11]);
        double cloudCoverHigh = parseDoubleOrNa(cols[12]);

        // 13: Wind_speed_10m (km/h)
        double windSpeed10m = parseDoubleOrNa(cols[13]);

        // 14: Shortwave_radiation (may be empty)
        var shortwaveRadiation = parseNullable(cols[14]);

        // 15: direct_radiation
        var directRadiation = parseNullable(cols[15]);

        // 16: diffuse_radiation
        var diffuseRadiation = parseNullable(cols[16]);

        // 17: direct_normal_irradiance
        Double directNormalIrradiance = parseNullable(cols[17]);

        // 18: daymax
        double dayMax = parseDoubleOrNa(cols[18]);

        var row = new DataRow(
          ts,
          pvMod1, pvMod2, pvMod3,
          demand, pv, price,
          temp, pressure,
          cloudCover, cloudCoverLow, cloudCoverMid, cloudCoverHigh,
          windSpeed10m,
          shortwaveRadiation,
          directRadiation,
          diffuseRadiation,
          directNormalIrradiance,
          dayMax
        );

        rows.add(row);
      }
    }

    return rows;
  }

  // Parse numeric; if empty, return NaN (you can decide later how to handle NaNs)
  private static double parseDoubleOrNa(String s) {
    s = s.trim();
    if (s.isEmpty()) {
      return Double.NaN;
    }
    return Double.parseDouble(s);
  }

  // Parse nullable Double; if empty, return null
  private static Double parseNullable(String s) {
    s = s.trim();
    if (s.isEmpty()) {
      return null;
    }
    return Double.parseDouble(s);
  }
}