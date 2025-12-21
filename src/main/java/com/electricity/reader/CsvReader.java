package com.electricity.reader;

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

  private static final DateTimeFormatter TS_FMT =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ssXXX");

  public static List<DataRow> loadTrainTest(Path csvPath) throws IOException {
    List<DataRow> rows = new ArrayList<>();

    try (BufferedReader br = Files.newBufferedReader(csvPath)) {
      String header = br.readLine();
      if (header == null) {
        throw new IllegalStateException("Empty CSV file: " + csvPath);
      }

      String line;
      while ((line = br.readLine()) != null) {
        String[] cols = line.split(",", -1);

        if (cols.length < 19) {
          throw new IllegalStateException(
            "Expected at least 19 columns, got " + cols.length + " in line: " + line
          );
        }

        OffsetDateTime ts = OffsetDateTime.parse(cols[0].trim(), TS_FMT);

        double pvMod1 = parseDoubleOrNa(cols[1]);
        double pvMod2 = parseDoubleOrNa(cols[2]);
        double pvMod3 = parseDoubleOrNa(cols[3]);
        double demand = parseDoubleOrNa(cols[4]);
        double pv = parseDoubleOrNa(cols[5]);
        double price = parseDoubleOrNa(cols[6]);
        double temp = parseDoubleOrNa(cols[7]);
        double pressure = parseDoubleOrNa(cols[8]);
        double cloudCover = parseDoubleOrNa(cols[9]);
        double cloudCoverLow = parseDoubleOrNa(cols[10]);
        double cloudCoverMid = parseDoubleOrNa(cols[11]);
        double cloudCoverHigh = parseDoubleOrNa(cols[12]);
        double windSpeed10m = parseDoubleOrNa(cols[13]);
        var shortwaveRadiation = parseNullable(cols[14]);
        var directRadiation = parseNullable(cols[15]);
        var diffuseRadiation = parseNullable(cols[16]);
        Double directNormalIrradiance = parseNullable(cols[17]);
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

  private static double parseDoubleOrNa(String s) {
    s = s.trim();
    if (s.isEmpty()) {
      return Double.NaN;
    }
    return Double.parseDouble(s);
  }

  private static Double parseNullable(String s) {
    s = s.trim();
    if (s.isEmpty()) {
      return null;
    }
    return Double.parseDouble(s);
  }
}