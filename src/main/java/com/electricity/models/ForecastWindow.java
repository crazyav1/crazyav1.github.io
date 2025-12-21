package com.electricity.models;

import java.util.List;

public final class ForecastWindow {

  public static List<ForecastRow> firstDays(List<ForecastRow> rows, int days) {
    int hours = days * 24;
    if (rows.size() <= hours) return rows;
    return rows.subList(0, hours);
  }

  private ForecastWindow() {}
}
