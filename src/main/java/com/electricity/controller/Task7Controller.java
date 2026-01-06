package com.electricity.controller;

import com.electricity.config.Task7Config;
import com.electricity.models.DataRow;
import com.electricity.models.SeriesResponse;
import com.electricity.reader.CsvReader;
import com.electricity.util.AcfPacf;
import com.electricity.util.Differencing;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static com.electricity.config.Constants.TRAIN_PATH;

@RestController
@RequestMapping("/api/task7")
public class Task7Controller {

  @GetMapping("/acf")
  @SneakyThrows
  public Map<String, Object> acf() {
    var rows = CsvReader.loadTrainTest(TRAIN_PATH);
    var y = rows.stream().mapToDouble(DataRow::demand).toArray();
    var dy = Differencing.diff1(y);
    var cfg = Task7Config.defaults();
    var acf = AcfPacf.acf(dy, cfg.maxLag());

    return createBarChart("ACF (stationary demand)", "Lag", "ACF", acf);
  }

  @GetMapping("/pacf")
  @SneakyThrows
  public Map<String, Object> pacf() {
    var rows = CsvReader.loadTrainTest(TRAIN_PATH);
    var y = rows.stream().mapToDouble(DataRow::demand).toArray();
    var dy = Differencing.diff1(y);
    var cfg = Task7Config.defaults();
    var pacf = AcfPacf.pacf(dy, cfg.maxLag());

    return createBarChart("PACF (stationary demand)", "Lag", "PACF", pacf);
  }

  private Map<String, Object> createBarChart(String title, String xTitle, String yTitle, double[] values) {
    var lags = IntStream.range(0, values.length).boxed().toList();
    var dataValues = Arrays.stream(values).boxed().toList();

    Map<String, Object> data = Map.of(
      "labels", lags,
      "datasets", List.of(
        Map.of(
          "label", yTitle,
          "data", dataValues,
          "borderWidth", 1
        )
      )
    );

    Map<String, Object> options = Map.of(
      "responsive", true,
      "maintainAspectRatio", false,
      "plugins", Map.of(
        "legend", Map.of("display", false),
        "title", Map.of("display", true, "text", title)
      ),
      "scales", Map.of(
        "x", Map.of("title", Map.of("display", true, "text", xTitle)),
        "y", Map.of("title", Map.of("display", true, "text", yTitle))
      )
    );

    return Map.of(
      "type", "bar",
      "data", data,
      "options", options
    );
  }
}
