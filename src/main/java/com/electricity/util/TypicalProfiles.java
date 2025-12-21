package com.electricity.util;

import com.electricity.models.DataRow;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public final class TypicalProfiles {

  public record Profile(double[] hourlyMean) {
  }

  public static Map<String, Profile> compute(List<DataRow> rows) {
    double[] weekdaySum = new double[24];
    int[] weekdayCnt = new int[24];

    double[] weekendSum = new double[24];
    int[] weekendCnt = new int[24];

    rows.forEach(r -> {
      double d = r.demand();
      if (Double.isNaN(d)) return;
      int h = r.timestamp().getHour();
      DayOfWeek dow = r.timestamp().getDayOfWeek();
      boolean isWeekend = (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY);
      if (isWeekend) {
        weekendSum[h] += d;
        weekendCnt[h]++;
      } else {
        weekdaySum[h] += d;
        weekdayCnt[h]++;
      }
    });

    double[] weekday = new double[24];
    double[] weekend = new double[24];

    IntStream.range(0, 24).forEach(h -> {
      weekday[h] = weekdayCnt[h] == 0 ? Double.NaN : weekdaySum[h] / weekdayCnt[h];
      weekend[h] = weekendCnt[h] == 0 ? Double.NaN : weekendSum[h] / weekendCnt[h];
    });

    return Map.of(
      "Weekday", new Profile(weekday),
      "Weekend", new Profile(weekend)
    );
  }

  public static void printTSV(Map<String, Profile> profiles) {
    System.out.println("Profile\tHour\tMeanDemand");
    profiles.forEach((name, p) -> IntStream.range(0, 24)
      .forEach(h ->
                 System.out.printf("%s\t%d\t%.6f%n", name, h, p.hourlyMean()[h])));
  }

  private TypicalProfiles() {
  }
}
