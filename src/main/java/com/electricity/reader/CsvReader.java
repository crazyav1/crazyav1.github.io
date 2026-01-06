package com.electricity.reader;

import com.electricity.models.DataRow;
import com.electricity.models.Task9ResultRow;
import com.electricity.util.TimeParsers;
import org.jspecify.annotations.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {

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

        var time = TimeParsers.parseOffsetDateTime(cols[0]);

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
          time,
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

  public static List<Task9ResultRow> loadTask9ResultRow(Path csvPath) throws IOException {
    List<Task9ResultRow> rows = new ArrayList<>();
    try (BufferedReader br = Files.newBufferedReader(csvPath)) {
      String header = br.readLine();
      if (header == null) {
        throw new IllegalStateException("Empty CSV file: " + csvPath);
      }

      String line;
      while ((line = br.readLine()) != null) {
        rows.add(getTask9ResultRow(line));
      }
      return rows;
    }
  }

  private static Task9ResultRow getTask9ResultRow(String line) {
    String[] cols = line.split(",", -1);

    if (cols.length < 1) {
      throw new IllegalStateException();
    }
    var day = cols[0];
    var hour = Integer.parseInt(cols[1]);
    var actual = Double.parseDouble(cols[2]);

    var ar = Double.parseDouble(cols[3]);
    var xgboost = Double.parseDouble(cols[4]);
    var naive = Double.parseDouble(cols[5]);
    var drift = Double.parseDouble(cols[6]);
    return new Task9ResultRow(day, hour, actual, ar, xgboost, naive, drift);
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