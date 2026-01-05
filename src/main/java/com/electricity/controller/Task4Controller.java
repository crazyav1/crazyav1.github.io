package com.electricity.controller;

import com.electricity.config.PvTask4Config;
import com.electricity.reader.CsvReader;
import com.electricity.util.PvMod1Imputation;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.electricity.config.Constants.TRAIN_PATH;

@RestController
@RequestMapping("/api/task4")
public class Task4Controller {

  @GetMapping("/imputation")
  @SneakyThrows
  public Map<String, Object> pvImputationChart(
    @RequestParam(defaultValue = "2013-07-01T00:00:00+00:00") OffsetDateTime start,
    @RequestParam(defaultValue = "2013-07-04T00:00:00+00:00") OffsetDateTime end) {

    var rows = CsvReader.loadTrainTest(TRAIN_PATH);
    var cfg = PvTask4Config.defaults();

    var res = PvMod1Imputation.runAll(rows, cfg);

    List<Map<String, Object>> origPoints = new ArrayList<>();
    List<Map<String, Object>> uniPoints = new ArrayList<>();
    List<Map<String, Object>> multiPoints = new ArrayList<>();
    List<String> labels = new ArrayList<>();

    double[] original = res.originalPvMod1();
    double[] uni = res.univariatePvMod1();
    double[] multi = res.multivariatePvMod1();

    for (int i = 0; i < rows.size(); i++) {
      var ts = rows.get(i).timestamp();
      if ((start == null || !ts.isBefore(start)) && (end == null || !ts.isAfter(end))) {
        String iso = ts.toString();
        origPoints.add(Map.of("x", iso, "y", original[i]));
        uniPoints.add(Map.of("x", iso, "y", uni[i]));
        multiPoints.add(Map.of("x", iso, "y", multi[i]));
        labels.add(iso);
      }
    }

    var datasets = List.of(
      Map.of(
        "label", "Original PV_mod1",
        "data", origPoints,
        "borderColor", "rgba(75,192,192,1)",
        "backgroundColor", "rgba(75,192,192,0.1)",
        "pointRadius", 0,
        "tension", 0.1
      ),
      Map.of(
        "label", "Univariate (linear)",
        "data", uniPoints,
        "borderColor", "rgba(255,159,64,1)",
        "backgroundColor", "rgba(255,159,64,0.08)",
        "pointRadius", 0,
        "tension", 0.1
      ),
      Map.of(
        "label", "Multivariate (OLS)",
        "data", multiPoints,
        "borderColor", "rgba(54,162,235,1)",
        "backgroundColor", "rgba(54,162,235,0.08)",
        "pointRadius", 0,
        "tension", 0.1
      )
    );

    var data = Map.of(
      "labels", labels,
      "datasets", datasets);

    var options = getChartOptions();

    return Map.of(
      "type", "line",
      "data", data,
      "options", options
    );
  }

  private static Map<String, Object> getChartOptions() {
    var xScale = Map.of(
      "title", Map.of("display", true, "text", "Time")
    );

    var yScale = Map.of(
      "title", Map.of("display", true, "text", "PV_mod1 (kW)")
    );

    return Map.of(
      "responsive", true,
      "maintainAspectRatio", false,
      "plugins", Map.of(
        "legend", Map.of("display", true),
        "title", Map.of("display", true, "text", "PV_mod1 before and after imputation"),
        "tooltip", Map.of(
          "mode", "index",
          "intersect", false
        )
      ),
      "scales", Map.of(
        "x", xScale,
        "y", yScale
      )
    );
  }
}
