package com.electricity.models;

import java.util.List;

public final class ForecastWindowExo {

  public static List<ForecastExoRow> firstDays(List<ForecastExoRow> rows, int days) {
    int hours = days * 24;
    return rows.size() <= hours ? rows : rows.subList(0, hours);
  }

  private ForecastWindowExo() {}
}
