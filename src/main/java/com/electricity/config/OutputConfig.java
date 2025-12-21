package com.electricity.config;

import java.nio.file.Path;

public record OutputConfig(Path dir, int dpi, boolean showOnScreen) {
  public static OutputConfig defaults(Path dir) {
    return new OutputConfig(dir, 300, true);
  }

  public record TimeSeriesConfig(int width, int height, int days, String datePattern, int xLabelRotationDeg) {
    public static TimeSeriesConfig defaults() {
      return new TimeSeriesConfig(1100, 650, 7, "dd.MM.yyyy HH:mm", 90);
    }
  }

  public record HistogramConfig(int width, int height, String xTitle, String yTitle, int xLabelRotationDeg) {
    public static HistogramConfig defaults() {
      return new HistogramConfig(900, 600, "Price: Euro/kWh", "Count", 90);
    }
  }
}