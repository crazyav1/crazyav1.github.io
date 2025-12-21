package com.electricity.config;

public record ArxConfig(
  int order,
  boolean includeHour,
  boolean includeWeekend,
  boolean includeWeather
) {
  public static ArxConfig defaults() {
    return new ArxConfig(
      24,
      true,
      true,
      true
    );
  }
}
