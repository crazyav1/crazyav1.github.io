package com.electricity.controller;

import com.electricity.models.Task11ScheduleRow;
import com.electricity.reader.Task11ScheduleCsvReader;
import com.electricity.util.Task11CostCalculator;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/api/task11")
public class Task11Controller {

  public static final Path LOW_PV_SCHEDULE_PATH = Path.of("figures/task11_schedule_pv_low.csv");
  public static final Path HIGH_PV_SCHEDULE_PATH = Path.of("figures/task11_schedule_pv_high.csv");

  @GetMapping("/costs/cumulative")
  @SneakyThrows
  public Map<String, Object> cumulativeCosts() {

    var low = Task11ScheduleCsvReader.load(LOW_PV_SCHEDULE_PATH);
    var high = Task11ScheduleCsvReader.load(HIGH_PV_SCHEDULE_PATH);

    double exportPriceFactor = 1.0;

    var costLow = Task11CostCalculator.costFromOptimisedSchedule(low, exportPriceFactor);
    var costHigh = Task11CostCalculator.costFromOptimisedSchedule(high, exportPriceFactor);
    var costNoPv = Task11CostCalculator.costWithoutPvBaseline(low);
    var costLowPvNoBatt = Task11CostCalculator.costPvNoBattery(low);
    var costHighPvNoBatt = Task11CostCalculator.costPvNoBattery(high);

    List<Integer> hours = IntStream.range(0, costLow.cumulative().length).boxed().toList();

    Map<String, Object> data = Map.of(
      "labels", hours,
      "datasets", List.of(
        Map.of(
          "label", "No PV (baseline)",
          "data", toList(costNoPv.cumulative()),
          "fill", false
        ),
        Map.of(
          "label", "PV low only (no battery)",
          "data", toList(costLowPvNoBatt.cumulative()),
          "fill", false
        ),
        Map.of(
          "label", "PV high only (no battery)",
          "data", toList(costHighPvNoBatt.cumulative()),
          "fill", false
        ),
        Map.of(
          "label", "PV_low + battery",
          "data", toList(costLow.cumulative()),
          "fill", false
        ),
        Map.of(
          "label", "PV_high + battery",
          "data", toList(costHigh.cumulative()),
          "fill", false
        )
      )
    );

    Map<String, Object> options = Map.of(
      "responsive", true,
      "maintainAspectRatio", false,
      "plugins", Map.of(
        "legend", Map.of("display", true),
        "title", Map.of("display", true, "text", "Cumulative Cost Comparison")
      ),
      "scales", Map.of(
        "x", Map.of("title", Map.of("display", true, "text", "Hour")),
        "y", Map.of("title", Map.of("display", true, "text", "Cumulative Cost (EUR)"))
      )
    );

    return Map.of(
      "type", "line",
      "data", data,
      "options", options
    );
  }

  @GetMapping("/costs/summary")
  @SneakyThrows
  public Map<String, Object> costSummary() {

    var low = Task11ScheduleCsvReader.load(LOW_PV_SCHEDULE_PATH);
    var high = Task11ScheduleCsvReader.load(HIGH_PV_SCHEDULE_PATH);

    double exportPriceFactor = 1.0;

    var costLow = Task11CostCalculator.costFromOptimisedSchedule(low, exportPriceFactor);
    var costHigh = Task11CostCalculator.costFromOptimisedSchedule(high, exportPriceFactor);
    var costNoPv = Task11CostCalculator.costWithoutPvBaseline(low);

    var labels = List.of("No PV", "PV_low", "PV_high");
    var values = List.of(costNoPv.total(), costLow.total(), costHigh.total());

    Map<String, Object> data = Map.of(
      "labels", labels,
      "datasets", List.of(
        Map.of(
          "label", "Total Cost (EUR)",
          "data", values
        )
      )
    );

    Map<String, Object> options = Map.of(
      "responsive", true,
      "maintainAspectRatio", false,
      "plugins", Map.of(
        "legend", Map.of("display", false),
        "title", Map.of("display", true, "text", "Total Cost Comparison")
      ),
      "scales", Map.of(
        "y", Map.of("title", Map.of("display", true, "text", "Cost (EUR)"), "beginAtZero", true)
      )
    );

    return Map.of(
      "type", "bar",
      "data", data,
      "options", options
    );
  }

  @GetMapping("/charge-discharge")
  @SneakyThrows
  public Map<String, Object> chargeDischarge() {

    var low = Task11ScheduleCsvReader.load(LOW_PV_SCHEDULE_PATH);
    var high = Task11ScheduleCsvReader.load(HIGH_PV_SCHEDULE_PATH);

    var hours = IntStream.range(0, low.size()).boxed().toList();
    var chargeLow = low.stream().map(Task11ScheduleRow::charge).toList();
    var dischargeLow = low.stream().map(Task11ScheduleRow::discharge).toList();
    var socLow = low.stream().map(Task11ScheduleRow::soc).toList();

    var chargeHigh = high.stream().map(Task11ScheduleRow::charge).toList();
    var dischargeHigh = high.stream().map(Task11ScheduleRow::discharge).toList();
    var socHigh = high.stream().map(Task11ScheduleRow::soc).toList();

    Map<String, Object> data = Map.of(
      "labels", hours,
      "datasets", List.of(
        Map.of(
          "label", "Charge Low PV(kW)",
          "data", chargeLow,
          "yAxisID", "yPower"
        ),
        Map.of(
          "label", "Discharge Low PV (kW)",
          "data", dischargeLow,
          "yAxisID", "yPower"
        ),
        Map.of(
          "label", "SOC Low PV (kWh)",
          "data", socLow,
          "type", "line",
          "fill", false,
          "yAxisID", "ySoc"
        ),
        Map.of(
          "label", "Charge High PV (kW)",
          "data", chargeHigh,
          "yAxisID", "yPower"
        ),
        Map.of(
          "label", "Discharge High PV (kW)",
          "data", dischargeHigh,
          "yAxisID", "yPower"
        ),
        Map.of(
          "label", "SOC High PV (kWh)",
          "data", socHigh,
          "type", "line",
          "fill", false,
          "yAxisID", "ySoc"
        )
      )
    );

    Map<String, Object> options = Map.of(
      "responsive", true,
      "maintainAspectRatio", false,
      "plugins", Map.of(
        "legend", Map.of("display", true),
        "title", Map.of("display", true, "text", "Battery Charge/Discharge & SOC")
      ),
      "scales", Map.of(
        "x", Map.of("title", Map.of("display", true, "text", "Hour")),
        "yPower", Map.of(
          "type", "linear",
          "position", "left",
          "title", Map.of("display", true, "text", "Power (kW)")
        ),
        "ySoc", Map.of(
          "type", "linear",
          "position", "right",
          "grid", Map.of("drawOnChartArea", false),
          "title", Map.of("display", true, "text", "SOC (kWh)")
        )
      )
    );

    return Map.of(
      "type", "bar",
      "data", data,
      "options", options
    );
  }

  private List<Double> toList(double[] arr) {
    return Arrays.stream(arr).boxed().toList();
  }
}
