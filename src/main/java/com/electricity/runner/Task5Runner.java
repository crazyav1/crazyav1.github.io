package com.electricity.runner;

import com.electricity.config.Constants;
import com.electricity.config.OutputConfig;
import com.electricity.config.Task5Config;
import com.electricity.models.DataRow;
import com.electricity.plotter.Task5Plotter;
import com.electricity.reader.CsvReader;
import com.electricity.service.StatsService;
import com.electricity.util.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public final class Task5Runner {

  private static final OutputConfig OUT =
    OutputConfig.defaults(Path.of("figures"));

  public static final Task5Config CFG =
    Task5Config.defaults();

 static void main() throws Exception {

    var rows = CsvReader.loadTrainTest(Constants.TRAIN_PATH);

    var statsTable = List.of(
      StatsService.labeled("Demand (kW)", rows, DataRow::demand),
      StatsService.labeled("Temperature (°C)", rows, DataRow::temperature),
      StatsService.labeled("Cloud cover (%)", rows, DataRow::cloudCover),
      StatsService.labeled("Shortwave radiation (W/m²)", rows, r -> safe(r.shortwaveRadiation())),
      StatsService.labeled("Wind speed 10m (km/h)", rows, DataRow::windSpeed10m),
      StatsService.labeled("Pressure (hPa)", rows, DataRow::pressure)
    );

    System.out.println("TASK 5 — DATA DESCRIPTION (STATS)");
    StatsService.printReport(statsTable);

    double[] demandRaw =
      FeatureEngineering.extract(rows, DataRow::demand);

    var jb = Normality.jarqueBera(demandRaw, CFG.normalityAlpha());

    System.out.println("TASK 5 — DISTRIBUTION CHECK (DEMAND)");
    System.out.printf(
      "Jarque-Bera JB=%.4f, p~%.6f, normal=%s%n",
      jb.jb(), jb.pValue(), jb.isNormal()
    );

    boolean useLog = !jb.isNormal();
    System.out.println(
      useLog
        ? "Decision: Demand is not normal -> apply log(Demand + eps)."
        : "Decision: Demand approximately normal → no transformation."
    );

    double[] demandUsed =
      useLog
        ? FeatureEngineering.log1pEps(demandRaw, CFG.logEps())
        : demandRaw;

    var bounds = OutlierCleaner.iqrBounds(demandUsed);
    boolean[] mask = OutlierCleaner.iqrMask(demandUsed);

    var rowsFiltered = new ArrayList<DataRow>();
    var demandFiltered = new ArrayList<Double>();

    for (int i = 0; i < rows.size(); i++) {
      if (mask[i]) {
        rowsFiltered.add(rows.get(i));
        demandFiltered.add(demandUsed[i]);
      }
    }

    System.out.printf(
      "Removed %d outliers (%.2f%%)%n",
      rows.size() - rowsFiltered.size(),
      100.0 * (rows.size() - rowsFiltered.size()) / rows.size()
    );

    System.out.printf(
      "Outlier bounds (IQR): [%.3f, %.3f]%n",
      bounds.lower(), bounds.upper()
    );

    System.out.println("TASK 5 — EXPORT FIGURES");

    Task5Plotter.exportDemandTimeSeries(rowsFiltered, CFG, OUT);

    double[] demandFilteredArr =
      demandFiltered.stream().mapToDouble(Double::doubleValue).toArray();

    Task5Plotter.exportDemandHistogram(demandFilteredArr, CFG, OUT);

    Task5Plotter.exportDemandVsTemperatureScatter(rowsFiltered, CFG, OUT);

    var dataset =
      FeatureEngineering.buildDataset(rowsFiltered, CFG, false);

    var ranking =
      FeatureRanking.rankByAbsPearson(dataset);

    System.out.println("TASK 5 — FEATURE RANKING (TSV FOR WORD)");
    FeatureRanking.printTSV(ranking);

    System.out.println("Saved figures to: " + OUT.dir().toAbsolutePath());
  }

  private static double safe(Double v) {
    return v == null ? Double.NaN : v;
  }

  private Task5Runner() {}
}
