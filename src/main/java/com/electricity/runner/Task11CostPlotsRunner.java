package com.electricity.runner;

import com.electricity.util.Task11CostCalculator;
import com.electricity.plotter.Task11CostPlotter;
import com.electricity.reader.Task11ScheduleCsvReader;

import java.nio.file.Path;

public final class Task11CostPlotsRunner {

  static void main() {
    var lowPath = Path.of("task11_schedule_pv_low.csv");
    var highPath = Path.of("task11_schedule_pv_high.csv");

    var low = Task11ScheduleCsvReader.load(lowPath);
    var high = Task11ScheduleCsvReader.load(highPath);

    double exportPriceFactor = 1.0;

    var costLow = Task11CostCalculator.costFromOptimisedSchedule(low, exportPriceFactor);
    var costHigh = Task11CostCalculator.costFromOptimisedSchedule(high, exportPriceFactor);

    var costNoPv = Task11CostCalculator.costWithoutPvBaseline(low);
    var costPvNoBatt = Task11CostCalculator.costPvNoBattery(low);

    System.out.println("TASK 11 - Total cost");
    System.out.println("Scenario\tTotalCost");
    System.out.printf("No PV (baseline)\t%.6f%n", costNoPv.total());
    System.out.printf("PV_low (optimised)\t%.6f%n", costLow.total());
    System.out.printf("PV_high (optimised)\t%.6f%n", costHigh.total());

    Task11CostPlotter.plotTotalCost(costNoPv.total(), costLow.total(), costHigh.total());
    Task11CostPlotter.plotCumulativeCost(
      costNoPv.cumulative(),
      costPvNoBatt.cumulative(),
      costLow.cumulative(),
      costHigh.cumulative()
    );

    System.out.println("TASK 11 - Total cost comparison");
    System.out.println("Scenario\tTotalCost");
    System.out.printf("No PV, no battery\t%.6f%n", costNoPv.total());
    System.out.printf("PV only (no battery)\t%.6f%n", costPvNoBatt.total());
    System.out.printf("PV_low + battery\t%.6f%n", costLow.total());
    System.out.printf("PV_high + battery\t%.6f%n", costHigh.total());
  }

  private Task11CostPlotsRunner() {}
}
