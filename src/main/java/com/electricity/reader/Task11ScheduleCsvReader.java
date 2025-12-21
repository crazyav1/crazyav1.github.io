package com.electricity.reader;

import com.electricity.models.Task11ScheduleRow;
import com.electricity.util.TimeParsers;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Task11ScheduleCsvReader {

  public static List<Task11ScheduleRow> load(Path csv) {
    try (BufferedReader br = Files.newBufferedReader(csv)) {
      String header = br.readLine();
      if (header == null) return List.of();

      var out = new ArrayList<Task11ScheduleRow>(64);

      String line;
      while ((line = br.readLine()) != null) {
        if (line.isBlank()) continue;

        var p = line.split(",", -1);
        if (p.length < 10) continue;

        var ts = TimeParsers.parseOffsetDateTime(p[0]);

        out.add(new Task11ScheduleRow(
          ts,
          parse(p[1]),
          parse(p[2]),
          parse(p[3]),
          parse(p[4]),
          parse(p[5]),
          parse(p[6]),
          parse(p[7]),
          parse(p[8]),
          parse(p[9])
        ));
      }

      return out;
    } catch (Exception e) {
      throw new IllegalStateException("Failed to read Task11 schedule csv: " + csv, e);
    }
  }

  private static double parse(String s) {
    if (s == null || s.isBlank()) {
      return Double.NaN;
    }
    return Double.parseDouble(s);
  }

  private Task11ScheduleCsvReader() {}
}
