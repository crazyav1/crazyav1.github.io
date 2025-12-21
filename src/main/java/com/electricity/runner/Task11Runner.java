package com.electricity.runner;

import com.electricity.config.Task11Config;
import com.electricity.exporter.Task11Exporter;
import com.electricity.models.OptimisationRow;
import com.electricity.plotter.Task11Plotter;
import com.electricity.reader.CsvReader;
import com.electricity.reader.OptimisationCsvReader;
import com.electricity.reader.Task11ScheduleCsvReader;
import com.electricity.util.*;

import java.io.IOException;

import static com.electricity.config.Constants.*;

public final class Task11Runner {

  static void main() throws IOException {
    var train = CsvReader.loadTrainTest(TRAIN_PATH);

    var optAll = OptimisationCsvReader.load(OPTIMIZATION_PATH);
    var opt24 = optAll.stream().limit(HORIZON_HOURS).toList();
    var demand24 = DemandForecasterForTask11.forecast24(train, opt24);

    var config = Task11Config.fromDefaults();

    var resLow = Task11BatteryOptimizer.optimise(opt24, demand24, config, OptimisationRow::pvLow);
    var resHigh = Task11BatteryOptimizer.optimise(opt24, demand24, config, OptimisationRow::pvHigh);

    Task11Exporter.writeScheduleCsv(PV_LOW_SCHEDULE_PATH, opt24, demand24, resLow, OptimisationRow::pvLow);
    Task11Exporter.writeScheduleCsv(PV_HIGH_SCHEDULE_PATH, opt24, demand24, resHigh, OptimisationRow::pvHigh);

    System.out.println("TASK 11 - Cost comparison");
    System.out.println("Scenario\tTotalCost");
    System.out.printf("PV_low\t%.6f%n", resLow.objectiveCost());
    System.out.printf("PV_high\t%.6f%n", resHigh.objectiveCost());

    System.out.println("Saved: " + PV_LOW_SCHEDULE_PATH);
    System.out.println("Saved: " + PV_HIGH_SCHEDULE_PATH);

    var low = Task11ScheduleCsvReader.load(PV_LOW_SCHEDULE_PATH);
    var high = Task11ScheduleCsvReader.load(PV_HIGH_SCHEDULE_PATH);
    Task11Plotter.plotAll(low, high, OUT_DIR);
    System.out.println("Saved plots to: " + OUT_DIR.toAbsolutePath());
  }

  private Task11Runner() {
  }
}
