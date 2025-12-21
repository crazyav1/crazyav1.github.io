package com.electricity.util;

import com.electricity.config.Task5Config;
import com.electricity.models.DataRow;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;

public final class FeatureEngineering {

  public record Dataset(double[] y, List<Feature> features) {}
  public record Feature(String name, double[] values) {}

  public static double[] extract(List<DataRow> rows, ToDoubleFunction<DataRow> f) {
    return rows.stream().mapToDouble(f).toArray();
  }

  public static double[] log1pEps(double[] x, double eps) {
    var out = new double[x.length];
    for (int i = 0; i < x.length; i++) {
      out[i] = Double.isNaN(x[i]) ? Double.NaN : Math.log(x[i] + eps);
    }
    return out;
  }

  public static Dataset buildDataset(List<DataRow> rows, Task5Config cfg, boolean useLogDemand) {
    var yRaw = rows.stream().mapToDouble(DataRow::demand).toArray();
    var y = useLogDemand ? log1pEps(yRaw, cfg.logEps()) : yRaw;

    var hour = new double[rows.size()];
    var isWeekend = new double[rows.size()];
    var temp = new double[rows.size()];
    var cooling = new double[rows.size()];
    var cloud = new double[rows.size()];
    var rad = new double[rows.size()];
    var effSolar = new double[rows.size()];
    var wind = new double[rows.size()];
    var pressure = new double[rows.size()];

    IntStream.range(0, rows.size()).forEach(i -> {
      var r = rows.get(i);
      hour[i] = r.timestamp().getHour();
      var dow = r.timestamp().getDayOfWeek();
      isWeekend[i] = (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) ? 1.0 : 0.0;
      var tObj = r.temperature();
      temp[i] = Double.isNaN(tObj) ? Double.NaN : tObj;
      cooling[i] = (Double.isNaN(temp[i])) ? Double.NaN : Math.max(0.0, temp[i] - cfg.comfortTempC());
      var cObj = r.cloudCover();
      cloud[i] = Double.isNaN(cObj) ? Double.NaN : cObj;
      var radObj = r.shortwaveRadiation();
      rad[i] = radObj == null ? Double.NaN : radObj;
      if (!Double.isNaN(rad[i]) && !Double.isNaN(cloud[i])) {
        effSolar[i] = rad[i] * (1.0 - clamp01(cloud[i] / 100.0));
      } else {
        effSolar[i] = Double.NaN;
      }
      var wObj = r.windSpeed10m();
      wind[i] = Double.isNaN(wObj) ? Double.NaN : wObj;
      var pObj = r.pressure();
      pressure[i] = Double.isNaN(pObj) ? Double.NaN : pObj;
    });

    var feats = new ArrayList<Feature>();
    feats.add(new Feature("hour_of_day", hour));
    feats.add(new Feature("is_weekend", isWeekend));
    feats.add(new Feature("temperature", temp));
    feats.add(new Feature("cooling_index", cooling));
    feats.add(new Feature("cloud_cover", cloud));
    feats.add(new Feature("shortwave_radiation", rad));
    feats.add(new Feature("effective_solar", effSolar));
    feats.add(new Feature("wind_speed_10m", wind));
    feats.add(new Feature("pressure", pressure));

    return new Dataset(y, List.copyOf(feats));
  }

  private static double clamp01(double v) {
    if (v < 0.0) {
      return 0.0;
    }
    return Math.min(v, 1.0);
  }

  private FeatureEngineering() {}
}
