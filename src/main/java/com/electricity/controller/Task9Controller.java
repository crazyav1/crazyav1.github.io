package com.electricity.controller;

import com.electricity.models.Task9ResultRow;
import com.electricity.reader.CsvReader;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Map;

@RestController
@RequestMapping("/api/task9")
public class Task9Controller {

  @GetMapping("/forecasts")
  @SneakyThrows
  public Map<String, Object> compareForecasts() {
    var rows = CsvReader.loadTask9ResultRow(Path.of("./src/main/resources/forecast_outputs_task9.csv"));
    var actual = rows.stream().map(Task9ResultRow::actual).toList();
    var ar = rows.stream().map(Task9ResultRow::ar).toList();
    var xgboost = rows.stream().map(Task9ResultRow::xgboost).toList();
    var naive = rows.stream().map(Task9ResultRow::naive).toList();
    var drift = rows.stream().map(Task9ResultRow::drift).toList();

    var datasets = new ArrayList<Map<String, Object>>();

    datasets.add(Map.of(
      "label", "Actual",
      "data", actual,
      "tension", 0.25,
      "pointRadius", 0
    ));

    datasets.add(Map.of(
      "label", "AR(24)",
      "data", ar,
      "tension", 0.25,
      "pointRadius", 0
    ));

    datasets.add(Map.of(
      "label", "XGBOOST)",
      "data", xgboost,
      "tension", 0.25,
      "pointRadius", 0
    ));

    datasets.add(Map.of(
      "label", "Naive",
      "data", naive,
      "tension", 0.25,
      "pointRadius", 0
    ));

    datasets.add(Map.of(
      "label", "Drift",
      "data", drift,
      "tension", 0.25,
      "pointRadius", 0
    ));

    Map<String, Object> data = Map.of(
      "labels", rows.stream().map(Task9ResultRow::getTimestamp).toList(),
      "datasets", datasets
    );

    Map<String, Object> options = Map.of(
      "responsive", true,
      "maintainAspectRatio", false,
      "plugins", Map.of(
        "legend", Map.of("display", true),
        "title", Map.of("display", true, "text", "Forecasts")
      ),
      "scales", Map.of(
        "x", Map.of("title", Map.of("display", true, "text", "Time")),
        "y", Map.of("title", Map.of("display", true, "text", "Demand (kW)"))
      )
    );

    return Map.of(
      "type", "line",
      "data", data,
      "options", options
    );
  }
}
