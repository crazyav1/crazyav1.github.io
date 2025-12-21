package com.electricity.runner;

import com.electricity.config.Task10Config;
import com.electricity.models.ArDemandModel;
import com.electricity.models.ForecastWindowExo;
import com.electricity.models.ModelSpec;
import com.electricity.reader.CsvReader;
import com.electricity.reader.ForecastExoCsvReader;
import com.electricity.util.*;

import java.util.List;

import static com.electricity.config.Constants.FORECAST_PATH;
import static com.electricity.config.Constants.TRAIN_PATH;

public final class Task10Runner {

  private static final Task10Config CFG = Task10Config.defaults();

  static void main() throws Exception {
    var trainRows = CsvReader.loadTrainTest(TRAIN_PATH);
    var history = PastData.fromTrainRows(trainRows);

    var forecast = ForecastExoCsvReader.load(FORECAST_PATH);
    var horizon = ForecastWindowExo.firstDays(forecast, CFG.days());
    var ar = new ArForecasterBaseline(new ArDemandModel("AR(24) on diff", 24));
    var arx = new ArxDiffForecaster(CFG.arx());

    var xgbLagsOnly = new XgbDirectForecasterExo(CFG.xgb(), false);
    var xgbWithExo  = new XgbDirectForecasterExo(CFG.xgb(), true);

    var models = List.of(
      ModelSpec.of("AR(24)", ar),
      ModelSpec.of("ARX(24)+exo", arx),
      ModelSpec.of("XGBoost(lags)", xgbLagsOnly),
      ModelSpec.of("XGBoost(lags+exo)", xgbWithExo)
    );

    var report = RollingOriginTask10.run(history, horizon, models, CFG);

    System.out.println("TASK 10 - MODELS WITH INPUTS (EXOGENOUS FEATURES)");
    System.out.println("Rolling-origin out-of-sample, 7 days, 24h horizon, 0h lead");

    System.out.println("OVERALL METRICS");
    Task10ReportPrinter.printOverallTSV(report);

    System.out.println("IMPROVEMENT OVER AUTOREGRESSIVE MODELS");
    Task10ReportPrinter.printImprovementsTSV(report, "AR(24)", "XGBoost(lags)");
  }

  private Task10Runner() {}
}
