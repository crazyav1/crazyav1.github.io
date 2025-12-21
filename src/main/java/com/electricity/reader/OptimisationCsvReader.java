package com.electricity.reader;

import com.electricity.models.OptimisationRow;
import com.electricity.util.TimeParsers;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class OptimisationCsvReader {

  public static List<OptimisationRow> load(Path csv) {
    try (BufferedReader br = Files.newBufferedReader(csv)) {
      var header = br.readLine();
      if (header == null) {
        return List.of();
      }

      var out = new ArrayList<OptimisationRow>(4096);
      String line;
      while ((line = br.readLine()) != null) {
        if (line.isBlank()) continue;
        var parts = line.split(",", -1);

        var ts = TimeParsers.parseOffsetDateTime(parts[0]);
        var pvLow = parseD(parts[1]);
        var pvHigh = parseD(parts[2]);
        var price = parseD(parts[3]);

        out.add(new OptimisationRow(ts, pvLow, pvHigh, price));
      }
      return out;
    } catch (Exception e) {
      throw new IllegalStateException("Failed to read optimisation csv: " + csv, e);
    }
  }

  private static double parseD(String s) {
    if (s == null || s.isBlank()) {
      return Double.NaN;
    }
    return Double.parseDouble(s);
  }

  private OptimisationCsvReader() {}
}
