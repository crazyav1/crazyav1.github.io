package com.electricity.util;

import com.electricity.config.Task9Config;
import com.electricity.models.ForecastRow;
import com.electricity.report.ForecastReport;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class RollingDayByDay {

  public static ForecastReport run(
    List<Double> fullHistory,
    List<ForecastRow> horizonRows,
    List<Forecaster.Named> models,
    Task9Config cfg
  ) throws Exception {

    int days = cfg.days();
    int horizon = cfg.horizonHours();

    var perDay = new ArrayList<ForecastReport.DayResult>();

    for (int d = 0; d < days; d++) {
      int startIdx = d * horizon;
      OffsetDateTime dayStart = horizonRows.get(startIdx).timestamp();

      double[] actual = new double[horizon];
      for (int i = 0; i < horizon; i++) {
        actual[i] = horizonRows.get(startIdx + i).demandActual();
      }

      var history = (List<Double>)HistoryWindow.apply(fullHistory, cfg.trainingWindow(), cfg.rollingWindowHours());

      var preds = new LinkedHashMap<String, double[]>();
      for (var m : models) {
        preds.put(m.name(), m.f().forecastNext(history, horizon, dayStart));
      }

      perDay.add(ForecastReport.DayResult.of(dayStart.toLocalDate().toString(), actual, preds));

      for (double v : actual) fullHistory.add(v);
    }

    return ForecastReport.from(perDay);
  }

  private RollingDayByDay() {}
}
