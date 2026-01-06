package com.electricity.models;

import java.time.OffsetDateTime;

public record Task9ResultRow(
  String day, int hour,Double actual, Double ar, Double xgboost, Double naive, Double drift
) {

  public String getTimestamp (){
      return String.format("%sT%02d:00:00Z", day, hour);
  }
}
