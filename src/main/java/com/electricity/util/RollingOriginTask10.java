package com.electricity.util;

import com.electricity.config.Task10Config;
import com.electricity.models.ForecastExoRow;
import com.electricity.models.ModelSpec;
import com.electricity.models.PastRow;
import com.electricity.report.Task10Report;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public final class RollingOriginTask10 {

  public static Task10Report run(
    List<PastRow> pastHistory,
    List<ForecastExoRow> horizonRows,
    List<ModelSpec> models,
    Task10Config cfg
  ) throws Exception {

    int days = cfg.days();
    int H = cfg.horizonHours();

    var dayResults = new ArrayList<Task10Report.DayResult>();

    for (int d = 0; d < days; d++) {
      int start = d * H;
      int end = start + H;

      var future24 = horizonRows.subList(start, end);

      var hist = (List<PastRow>)HistoryWindow.apply(pastHistory, cfg.trainingWindow(), cfg.rollingWindowHours());

      double[] actual = new double[H];
      for (int i = 0; i < H; i++) actual[i] = future24.get(i).demandActual();

      var preds = new LinkedHashMap<String, double[]>();
      for (var m : models) {
        preds.put(m.name(), m.forecaster().forecast24(hist, future24));
      }

      dayResults.add(Task10Report.DayResult.of(future24.getFirst().timestamp().toLocalDate().toString(), actual, preds));

      for (var r : future24) {
        pastHistory.add(new PastRow(r.timestamp(), r.demandActual(), r.temperature(), r.cloudCover(), r.windSpeed10m(), r.pressure()));
      }
    }

    return Task10Report.from(dayResults);
  }

  private RollingOriginTask10() {}
}
