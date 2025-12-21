package com.electricity.runner;

import com.electricity.config.Task9Config;
import com.electricity.exporter.ForecastExporter;
import com.electricity.models.ArDemandModel;
import com.electricity.models.ForecastWindow;
import com.electricity.reader.CsvReader;
import com.electricity.reader.ForecastCsvReader;
import com.electricity.util.*;

import java.util.ArrayList;
import java.util.List;

import static com.electricity.config.Constants.FORECAST_PATH;
import static com.electricity.config.Constants.TRAIN_PATH;

public final class Task9Runner {

  private static final Task9Config CFG = Task9Config.defaults();

  static void main() throws Exception {

    var trainRows = CsvReader.loadTrainTest(TRAIN_PATH);
    var forecastRows = ForecastCsvReader.load(FORECAST_PATH);
    var horizonRows = ForecastWindow.firstDays(forecastRows, CFG.days());

    var history = new ArrayList<Double>(trainRows.size() + horizonRows.size());
    trainRows.forEach(r -> history.add(r.demand()));

    var stat = new ArDemandModel("AR(24) on diff", 24);

    var models = List.of(
      Forecaster.named(stat.name(), new ArForecasterAdapter(stat)),
      Forecaster.named("XGBoost(direct)", new XgbDirectForecaster(CFG.xgbCfg())),
      Forecaster.named("Naive", new NaiveForecaster()),
      Forecaster.named("Drift", new DriftForecaster())
    );

    var report = RollingDayByDay.run(history, horizonRows, models, CFG);

    System.out.println("TASK 9 — Rolling-origin out-of-sample forecast (7 days, 24h horizon, 0h lead)");
    System.out.println("OVERALL");
    ReportPrinter.printOverallTSV(report);

    System.out.println("PER-DAY");
    ReportPrinter.printPerDayTSV(report);

    ForecastExporter.saveCsv(report, java.nio.file.Path.of("forecast_outputs_task9.csv"));
    System.out.println("Saved: forecast_outputs_task9.csv");
  }

  private Task9Runner() {}
}
