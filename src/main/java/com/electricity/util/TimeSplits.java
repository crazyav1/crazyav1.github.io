package com.electricity.util;

public final class TimeSplits {

  public record Split(int trainEnd) {}

  public static Split lastFraction(int totalRows, double holdoutFraction) {
    int trainEnd = (int) Math.floor(totalRows * (1.0 - holdoutFraction));
    trainEnd = Math.max(1, Math.min(totalRows - 1, trainEnd));
    return new Split(trainEnd);
  }

  private TimeSplits() {}
}
