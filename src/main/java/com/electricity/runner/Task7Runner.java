package com.electricity.runner;

import com.electricity.config.OutputConfig;
import com.electricity.config.Task7Config;
import com.electricity.models.ArDemandModel;
import com.electricity.models.DataRow;
import com.electricity.plotter.ACFPACFPlotter;
import com.electricity.reader.CsvReader;
import com.electricity.util.*;

import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.util.List;

import static com.electricity.config.Constants.TRAIN_PATH;

public final class Task7Runner {

  private static final OutputConfig OUT = OutputConfig.defaults(Path.of("figures"));
  private static final Task7Config CFG = Task7Config.defaults();

  static void main() throws Exception {
    var rows = CsvReader.loadTrainTest(TRAIN_PATH);

    var t = rows.stream().map(DataRow::timestamp).toArray(OffsetDateTime[]::new);
    var y = rows.stream().mapToDouble(DataRow::demand).toArray();
    var dy = Differencing.diff1(y);

    var acf = AcfPacf.acf(dy, CFG.maxLag());
    var pacf = AcfPacf.pacf(dy, CFG.maxLag());
    ACFPACFPlotter.plot(acf, pacf, OUT);

    var modelA = new ArDemandModel("AR(1) on diff", 1);
    var modelB = new ArDemandModel("AR(24) on diff", 24);


    var nrmseA_all = TimeSeriesValidationAR.rollingOneStepNRMSE(y, modelA, CFG.minTrainSize());
    var nrmseB_all = TimeSeriesValidationAR.rollingOneStepNRMSE(y, modelB, CFG.minTrainSize());

    System.out.println("Rolling one-step validation on whole training set");
    System.out.printf("%s : NRMSE=%.6f%n", modelA.name(), nrmseA_all);
    System.out.printf("%s : NRMSE=%.6f%n", modelB.name(), nrmseB_all);


    var nrmseA_wf = TimeSeriesValidationAR.walkForwardLastWeekDailyNRMSE(y, modelA);
    var nrmseB_wf = TimeSeriesValidationAR.walkForwardLastWeekDailyNRMSE(y, modelB);

    System.out.println("Walk-forward last week (daily folds, 24h forecast)");
    System.out.printf("%s : NRMSE=%.6f%n", modelA.name(), nrmseA_wf);
    System.out.printf("%s : NRMSE=%.6f%n", modelB.name(), nrmseB_wf);

    var winner = (nrmseA_wf <= nrmseB_wf) ? modelA.name() : modelB.name();
    System.out.println("Winner (lower walk-forward NRMSE): " + winner);
    System.out.println("Figures saved in: " + OUT.dir().toAbsolutePath());
  }

  private Task7Runner() {}
}
