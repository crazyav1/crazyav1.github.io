package com.electricity.controller;

import com.electricity.models.HistogramMethods;
import com.electricity.reader.CsvReader;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.electricity.config.Constants.TRAIN_PATH;
import static com.electricity.util.HistogramUtils.computeBins;

@RestController
@RequestMapping("/api/task3")
public class Task3Controller {


  @GetMapping("/price-histogram")
  @SneakyThrows
  public Map<String, Object> priceHistogram(@RequestParam(defaultValue = "STURGES") HistogramMethods method) {
    var rows = CsvReader.loadTrainTest(TRAIN_PATH);

    var prices = rows.stream()
                   .map(dataRow -> dataRow.price() * 1000)
                   .filter(p -> !Double.isNaN(p))
                   .toList();

    if (prices.isEmpty()) {
      return emptyBarChart();
    }

    var bins = computeBins(prices, method);
    double min = prices.stream().mapToDouble(Double::doubleValue).min().orElse(0);
    double max = prices.stream().mapToDouble(Double::doubleValue).max().orElse(0);

    if (max <= min) {
      bins = 1;
      max = min + 1e-6;
    }

    double width = (max - min) / bins;

    int[] counts = new int[bins];
    for (double v : prices) {
      int idx = (int) Math.floor((v - min) / width);
      if (idx < 0) idx = 0;
      if (idx >= bins) idx = bins - 1; // include max
      counts[idx]++;
    }

    var labels = new ArrayList<String>(bins);
    for (int i = 0; i < bins; i++) {
      double a = min + i * width;
      double b = min + (i + 1) * width;
      labels.add(String.format("%.0f–%.0f", a, b));
    }

    var values = new ArrayList<Integer>(bins);
    for (int c : counts) values.add(c);

    Map<String, Object> data = Map.of(
      "labels", labels,
      "datasets", List.of(
        Map.of(
          "label", "Price (EUR/MWh)",
          "data", values
        )
      )
    );

    Map<String, Object> options = Map.of(
      "responsive", true,
      "maintainAspectRatio", false,
      "plugins", Map.of(
        "legend", Map.of("display", true),
        "title", Map.of("display", true, "text", "Price distribution")
      ),
      "scales", Map.of(
        "x", Map.of(
          "title", Map.of("display", true, "text", "Price (EUR/MWh)"),
          "ticks", Map.of("maxRotation", 90, "minRotation", 90)
        ),
        "y", Map.of(
          "title", Map.of("display", true, "text", "Count"),
          "beginAtZero", true
        )
      )
    );

    return Map.of(
      "type", "bar",
      "data", data,
      "options", options
    );
  }


  @GetMapping("/demand-vs-temperature")
  @SneakyThrows
  public Map<String, Object> demandVsTemperature(@RequestParam(defaultValue = "4") int markerSize) {
    var rows = CsvReader.loadTrainTest(TRAIN_PATH);

    var points = rows.stream()
                   .filter(r -> !Double.isNaN(r.temperature()) && !Double.isNaN(r.demand()))
                   .map(r -> Map.<String, Object>of("x", r.temperature(), "y", r.demand()))
                   .toList();

    if (points.isEmpty()) {
      return emptyScatterChart();
    }

    Map<String, Object> data = Map.of(
      "datasets", List.of(
        Map.of(
          "label", "Demand vs Temperature",
          "data", points,
          "pointRadius", markerSize
        )
      )
    );

    Map<String, Object> options = Map.of(
      "responsive", true,
      "maintainAspectRatio", false,
      "plugins", Map.of(
        "legend", Map.of("display", false),
        "title", Map.of("display", true, "text", "Demand vs Temperature")
      ),
      "scales", Map.of(
        "x", Map.of(
          "type", "linear",
          "position", "bottom",
          "title", Map.of("display", true, "text", "Temperature (°C)")
        ),
        "y", Map.of(
          "title", Map.of("display", true, "text", "Demand (kW)"),
          "beginAtZero", false
        )
      )
    );

    return Map.of(
      "type", "scatter",
      "data", data,
      "options", options
    );
  }

  private static Map<String, Object> emptyScatterChart() {
    return Map.of(
      "type", "scatter",
      "data", Map.of("datasets", List.of(Map.of("label", "Demand vs Temperature", "data", List.of()))),
      "options", Map.of(
        "plugins", Map.of("title", Map.of("display", true, "text", "Demand vs Temperature")),
        "scales", Map.of(
          "x", Map.of("title", Map.of("display", true, "text", "Temperature (°C)")),
          "y", Map.of("title", Map.of("display", true, "text", "Demand (kW)"), "beginAtZero", true)
        )
      )
    );
  }

  private static Map<String, Object> emptyBarChart() {
    return Map.of(
      "type", "bar",
      "data", Map.of("labels", List.of(), "datasets", List.of(Map.of("label", "Price (EUR/MWh)", "data", List.of()))),
      "options", Map.of(
        "plugins", Map.of("title", Map.of("display", true, "text", "Price distribution")),
        "scales", Map.of(
          "x", Map.of("title", Map.of("display", true, "text", "Price (EUR/MWh)")),
          "y", Map.of("title", Map.of("display", true, "text", "Count"), "beginAtZero", true)
        )
      )
    );
  }
}
