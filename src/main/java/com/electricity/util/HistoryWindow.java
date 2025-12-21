package com.electricity.util;

import java.util.List;

import static com.electricity.util.TrainingWindow.EXPANDING;

public final class HistoryWindow {

  public static List<?> apply(List<?> fullHistory, TrainingWindow trainingWindow, int rollingWindowHours) {
    if (trainingWindow == EXPANDING) {
      return fullHistory;
    }

    int n = fullHistory.size();
    int keep = Math.min(n, rollingWindowHours);
    return fullHistory.subList(n - keep, n);
  }

  private HistoryWindow() {}
}
