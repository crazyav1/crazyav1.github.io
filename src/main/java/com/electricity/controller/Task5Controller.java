package com.electricity.controller;

import com.electricity.models.DataRow;
import com.electricity.models.HistogramMethods;
import com.electricity.models.MetricPoint;
import com.electricity.models.SeriesResponse;
import com.electricity.reader.CsvReader;
import com.electricity.util.FeatureEngineering;
import com.electricity.util.OutlierCleaner;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.electricity.config.Constants.TRAIN_PATH;
import static com.electricity.runner.Task5Runner.CFG;
import static com.electricity.util.HistogramUtils.computeBins;

@RestController
@RequestMapping("/api/task5")
public class Task5Controller {

  @GetMapping("/demand/distribution")
  @SneakyThrows
  public Map<String, Object> demandHistogramChart(@RequestParam(defaultValue = "STURGES") HistogramMethods method) {

    var rows = CsvReader.loadTrainTest(TRAIN_PATH);

    double[] demandRaw =
      FeatureEngineering.extract(rows, DataRow::demand);

    double[] demandUsed = FeatureEngineering.log1pEps(demandRaw, CFG.logEps());
    boolean[] mask = OutlierCleaner.iqrMask(demandUsed);
    var values = new ArrayList<Double>();

    for (int i = 0; i < rows.size(); i++) {
      if (mask[i]) {
        values.add(demandUsed[i]);
      }
    }


    int bins = computeBins(values, method);
    double min = values.stream().mapToDouble(Double::doubleValue).min().orElse(0.0);
    double max = values.stream().mapToDouble(Double::doubleValue).max().orElse(min);

    // build bins
    int[] counts = new int[bins];
    List<String> labels = new ArrayList<>(bins);
    if (min == max) {

      counts[0] = values.size();
      labels.add(String.format("%.2f", min));
      for (int i = 1; i < bins; i++) labels.add("");
    } else {
      double width = (max - min) / bins;
      for (double v : values) {
        int idx = (int) ((v - min) / width);
        if (idx < 0) idx = 0;
        if (idx >= bins) idx = bins - 1;
        counts[idx]++;
      }
      for (int i = 0; i < bins; i++) {
        double a = min + i * width;
        double b = (i == bins - 1) ? max : (a + width);
        labels.add(String.format("%.2f - %.2f", a, b));
      }
    }

    var dataCounts = new ArrayList<>(bins);
    for (int c : counts) dataCounts.add(c);

    var dataset = Map.of(
      "label", "Demand",
      "data", dataCounts,
      "backgroundColor", "rgba(54,162,235,0.6)",
      "borderColor", "rgba(54,162,235,1)",
      "borderWidth", 1
    );

    var data = Map.of("labels", labels, "datasets", List.of(dataset));

    var options = Map.of(
      "responsive", true,
      "maintainAspectRatio", false,
      "plugins", Map.of(
        "legend", Map.of("display", false),
        "title", Map.of("display", true, "text", "Demand histogram")
      ),
      "scales", Map.of(
        "x", Map.of("title", Map.of("display", true, "text", "Demand")),
        "y", Map.of("title", Map.of("display", true, "text", "Count"), "beginAtZero", true)
      )
    );

    return Map.of("type", "bar", "data", data, "options", options);
  }
}
