package com.electricity.controller;

import com.electricity.models.SeriesResponse;
import com.electricity.reader.CsvReader;
import com.electricity.util.ClassicalDecomposition;
import com.electricity.util.TypicalProfiles;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.electricity.config.Constants.TRAIN_PATH;

@RestController
@RequestMapping("/api/task6")
public class Task6Controller {

  @GetMapping("/decomposition")
  @SneakyThrows
  public Map<String, Object> decomposition() {
    var rows = CsvReader.loadTrainTest(TRAIN_PATH);
    var dec = ClassicalDecomposition.decompose(rows, ClassicalDecomposition.Settings.defaults());

    var x = rows.stream().map(r -> r.timestamp().toInstant()).toList();

    int start = (x.size() / 2) - 120;
    int end = (x.size() / 2) + 120;

    var x24 = x.subList(start, end);
    var observed24 = Arrays.copyOfRange(dec.observed(), start, end);
    var trend24 = Arrays.copyOfRange(dec.trend(), start, end);
    var seasonal24 = Arrays.copyOfRange(dec.seasonal(), start, end);
    var residual24 = Arrays.copyOfRange(dec.residual(), start, end);

    Map<String, Object> data = Map.of(
      "labels", x24,
      "datasets", List.of(
        Map.of(
          "label", "Observed demand (kW)",
          "data", toList(observed24),
          "yAxisID", "y",
          "pointRadius", 0,
          "tension", 0.25
        ),
        Map.of(
          "label", "Trend (Tt)",
          "data", toList(trend24),
          "yAxisID", "y",
          "pointRadius", 0,
          "tension", 0.25
        ),
        Map.of(
          "label", "Seasonality (St)",
          "data", toList(seasonal24),
          "yAxisID", "y",
          "pointRadius", 0,
          "tension", 0.25
        ),
        Map.of(
          "label", "Residual (Rt)",
          "data", toList(residual24),
          "yAxisID", "y",
          "pointRadius", 0,
          "tension", 0.25
        )
      )
    );

    Map<String, Object> options = Map.of(
      "responsive", true,
      "maintainAspectRatio", false,
      "interaction", Map.of("mode", "index", "intersect", false),
      "plugins", Map.of(
        "legend", Map.of("display", true),
        "title", Map.of("display", true, "text", "Decomposition (Observed, Trend, Seasonal, Residual)")
      ),
      "scales", Map.of(
        "x", Map.of("ticks", Map.of("maxRotation", 90, "minRotation", 90)),
        "y", Map.of(
          "type", "linear",
          "position", "left",
          "title", Map.of("display", true, "text", "Value")
        )
      )
    );

    return Map.of(
      "type", "line",
      "data", data,
      "options", options
    );
  }

  @GetMapping("/typical-profiles")
  @SneakyThrows
  public Map<String, Object> typicalProfiles() {
    var rows = CsvReader.loadTrainTest(TRAIN_PATH);
    var profiles = TypicalProfiles.compute(rows);
    var hours = IntStream.range(0, 24).boxed().toList();

    var datasets = new ArrayList<>();
    
    var weekday = profiles.get("Weekday");
    if (weekday != null) {
      datasets.add(Map.of(
        "label", "Weekday",
        "data", toList(weekday.hourlyMean()),
        "tension", 0.4
      ));
    }

    var weekend = profiles.get("Weekend");
    if (weekend != null) {
      datasets.add(Map.of(
        "label", "Weekend",
        "data", toList(weekend.hourlyMean()),
        "tension", 0.4
      ));
    }

    Map<String, Object> data = Map.of(
      "labels", hours,
      "datasets", datasets
    );

    Map<String, Object> options = Map.of(
      "responsive", true,
      "maintainAspectRatio", false,
      "plugins", Map.of(
        "legend", Map.of("display", true),
        "title", Map.of("display", true, "text", "Typical Demand Profiles")
      ),
      "scales", Map.of(
        "x", Map.of("title", Map.of("display", true, "text", "Hour of Day")),
        "y", Map.of("title", Map.of("display", true, "text", "Mean Demand (kW)"))
      )
    );

    return Map.of(
      "type", "line",
      "data", data,
      "options", options
    );
  }

  private List<Double> toList(double[] arr) {
    return Arrays.stream(arr).boxed().toList();
  }
}
