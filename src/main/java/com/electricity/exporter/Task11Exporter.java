package com.electricity.exporter;

import com.electricity.models.DemandForecastRow;
import com.electricity.models.OptimisationRow;
import com.electricity.util.Task11BatteryOptimizer;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public final class Task11Exporter {

  public static void writeScheduleCsv(
    Path out,
    List<OptimisationRow> opt24,
    List<DemandForecastRow> demand24,
    Task11BatteryOptimizer.Result r,
    Function<OptimisationRow, Double> pvSelector
  ) {
    try (BufferedWriter bw = Files.newBufferedWriter(out)) {
      bw.write("timestamp,price,pv_forecast,demand_forecast,pv_used,grid_import,grid_export,charge,discharge,soc");
      bw.write(System.lineSeparator());
      for (int t = 0; t < opt24.size(); t++) {
        var o = opt24.get(t);
        var d = demand24.get(t);

        bw.write("%s,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f,%.6f%n".formatted(
          o.timestamp(),
          o.price(),
          nz(pvSelector.apply(o)),
          d.demandForecast(),
          r.pvUsed().get(t),
          r.gridImport().get(t),
          r.gridExport().get(t),
          r.charge().get(t),
          r.discharge().get(t),
          r.soc().get(t)
        ));
      }
    } catch (Exception e) {
      throw new IllegalStateException("Failed writing schedule csv: " + out, e);
    }
  }

  private static double nz(Double v) {
    if (v == null || Double.isNaN(v) || Double.isInfinite(v)) return 0.0;
    return v;
  }

  private Task11Exporter() {}
}
