package com.electricity.controller;

import com.electricity.plotter.TimeSeriesPlotter;
import com.electricity.reader.CsvReader;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static com.electricity.config.Constants.TRAIN_PATH;

@RestController
@RequestMapping("/api/task1")
public class Task1Controller {

  @GetMapping("/price-demand-pv")
  @SneakyThrows
  public Map<String, Object> demandPvPrice(
    @RequestParam(defaultValue = "2013-07-01T00:00:00+00:00") OffsetDateTime start,
    @RequestParam(defaultValue = "2013-07-08T00:00:00+00:00") OffsetDateTime end) {
    var dataRows = CsvReader.loadTrainTest(TRAIN_PATH);
    var subset = dataRows.stream()
                   .filter(
                     r -> r.timestamp().isAfter(start) &&
                            r.timestamp().isBefore(end)
                   ).toList();
    var aligned = TimeSeriesPlotter.align(subset);
    Map<String, Object> data = Map.of(
      "labels", aligned.time(),
      "datasets", List.of(
        Map.of(
          "label", "Demand (kW)",
          "data", aligned.demand(),
          "yAxisID", "yPower",
          "tension", 0.25,
          "pointRadius", 0,
          "spanGaps", true
        ),
        Map.of(
          "label", "PV (kW)",
          "data", aligned.pv(),
          "yAxisID", "yPower",
          "tension", 0.25,
          "pointRadius", 0,
          "spanGaps", true
        ),
        Map.of(
          "label", "Price (Euro/kWh)",
          "data", aligned.price(),
          "yAxisID", "yPrice",
          "tension", 0.25,
          "pointRadius", 0,
          "spanGaps", true
        )
      )
    );

    Map<String, Object> options = Map.of(
      "responsive", true,
      "maintainAspectRatio", false,
      "interaction", Map.of("mode", "index", "intersect", false),
      "plugins", Map.of("legend", Map.of("display", true)),
      "scales", Map.of(
        "x", Map.of("ticks", Map.of("maxRotation", 90, "minRotation", 90)),
        "yPower", Map.of(
          "type", "linear",
          "position", "left",
          "title", Map.of("display", true, "text", "Power (kW)")
        ),
        "yPrice", Map.of(
          "type", "linear",
          "position", "right",
          "grid", Map.of("drawOnChartArea", false),
          "title", Map.of("display", true, "text", "Price (Euro/kWh)")
        )
      )
    );

    return Map.of(
      "type", "line",
      "data", data,
      "options", options
    );
  }
}
