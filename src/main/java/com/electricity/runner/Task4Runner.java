package com.electricity.runner;

import com.electricity.config.OutputConfig;
import com.electricity.config.PvTask4Config;
import com.electricity.models.Stats;
import com.electricity.plotter.PvBeforeAfterPlotter;
import com.electricity.reader.CsvReader;
import com.electricity.report.PvQualityReport;
import com.electricity.util.*;

import java.nio.file.Path;
import java.util.List;

import static com.electricity.config.Constants.TRAIN_PATH;

public final class Task4Runner {

  private static final OutputConfig OUT = OutputConfig.defaults(Path.of("figures"));
  private static final PvTask4Config CFG = PvTask4Config.defaults();

  static void main() throws Exception {
    var rows = CsvReader.loadTrainTest(TRAIN_PATH);

    PvQualityReport.print(rows, CFG);
    PvQualityReport.printDaylightOnly(rows, CFG);

    var evidence = MissingnessMechanismAnalyzer.analyzePvMod1(rows, CFG);
    MissingnessMechanismAnalyzer.printReport(evidence);

    var res = PvMod1Imputation.runAll(rows, CFG);

    Task4StatsTablePrinter.printReport(List.of(
      new Task4StatsTablePrinter.Row("Original", Stats.of(res.originalPvMod1())),
      new Task4StatsTablePrinter.Row("Deletion", Stats.of(res.deletionPvMod1())),
      new Task4StatsTablePrinter.Row("Univariate", Stats.of(res.univariatePvMod1())),
      new Task4StatsTablePrinter.Row("Multivariate", Stats.of(res.multivariatePvMod1()))
    ));

    var plotter = new PvBeforeAfterPlotter(OUT);
    var hours = (int) (CFG.representativeWindow().toHours());
    plotter.plot(rows, res.originalPvMod1(), res.univariatePvMod1(), res.multivariatePvMod1(), 0, hours);

    System.out.println("Task 4 outputs saved to: " + OUT.dir().toAbsolutePath());
  }

  private Task4Runner() {}
}
