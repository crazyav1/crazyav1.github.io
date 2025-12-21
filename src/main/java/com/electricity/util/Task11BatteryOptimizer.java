package com.electricity.util;

import com.electricity.config.BatterySpec;
import com.electricity.models.DemandForecastRow;
import com.electricity.models.OptimisationRow;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

public final class Task11BatteryOptimizer {

  public record Result(
    List<Double> soc,
    List<Double> charge,
    List<Double> discharge,
    List<Double> gridImport,
    List<Double> gridExport,
    List<Double> pvUsed,
    double objectiveCost
  ) {
  }

  public static Result optimise(
    List<OptimisationRow> optRows,
    List<DemandForecastRow> demandForecast,
    BatterySpec b,
    double exportPriceFactor,
    double gridLimitKW,
    Function<OptimisationRow, Double> pvSelector
  ) {

    if (optRows.size() != demandForecast.size()) {
      throw new IllegalArgumentException("optRows and demandForecast must have same size (24 hours).");
    }
    int n = optRows.size();

    var model = new ExpressionsBasedModel();

    var soc = new Variable[n + 1];
    var charge = new Variable[n];
    var discharge = new Variable[n];
    var gridIn = new Variable[n];
    var gridOut = new Variable[n];
    var pvUsed = new Variable[n];

    Expression obj = model.addExpression("objective").weight(BigDecimal.ONE);

    for (int t = 0; t <= n; t++) {
      soc[t] = model.addVariable("soc_" + t)
                 .lower(bd(b.minSocKWh()))
                 .upper(bd(b.maxSocKWh()));
    }

    model.addExpression("soc_init")
      .level(bd(b.initialSocKWh()))
      .set(soc[0], BigDecimal.ONE);

    var gridMode = new Variable[n]; // binary per hour
    for (int t = 0; t < n; t++) {
      gridMode[t] = model.addVariable("gridMode_" + t)
                      .binary();

      charge[t] = model.addVariable("ch_" + t)
                    .lower(BigDecimal.ZERO)
                    .upper(bd(b.chargeLimitKW()));

      discharge[t] = model.addVariable("dis_" + t)
                       .lower(BigDecimal.ZERO)
                       .upper(bd(b.dischargeLimitKW()));

      gridIn[t] = model.addVariable("gridIn_" + t)
                    .lower(BigDecimal.ZERO)
                    .upper(bd(gridLimitKW));

      gridOut[t] = model.addVariable("gridOut_" + t)
                     .lower(BigDecimal.ZERO)
                     .upper(bd(gridLimitKW));

      pvUsed[t] = model.addVariable("pvUsed_" + t)
                    .lower(BigDecimal.ZERO);

      model.addExpression("grid_import_limit_" + t)
        .upper(bd(gridLimitKW))
        .set(gridIn[t], BigDecimal.ONE)
        .set(gridMode[t], bd(-gridLimitKW));

      model.addExpression("grid_export_limit_" + t)
        .upper(bd(gridLimitKW))
        .set(gridOut[t], BigDecimal.ONE)
        .set(gridMode[t], bd(gridLimitKW));

      double pvAvail = safeNonNeg(pvSelector.apply(optRows.get(t)));
      model.addExpression("pv_cap_" + t)
        .upper(bd(pvAvail))
        .set(pvUsed[t], BigDecimal.ONE);

      model.addExpression("soc_dyn_" + t)
        .level(BigDecimal.ZERO)
        .set(soc[t + 1], BigDecimal.ONE)
        .set(soc[t], BigDecimal.ONE.negate())
        .set(charge[t], bd(-b.etaCharge()))
        .set(discharge[t], bd(1.0 / b.etaDischarge()));

      double demand = demandForecast.get(t).demandForecast();
      model.addExpression("balance_" + t)
        .level(bd(demand))
        .set(gridIn[t], BigDecimal.ONE)
        .set(pvUsed[t], BigDecimal.ONE)
        .set(discharge[t], BigDecimal.ONE)
        .set(charge[t], BigDecimal.ONE.negate())
        .set(gridOut[t], BigDecimal.ONE.negate());

      double price = safeNonNeg(optRows.get(t).price());
      obj.set(gridIn[t], bd(price));
      obj.set(gridOut[t], bd(-exportPriceFactor * price));
    }

    Optimisation.Result solution = model.minimise();

    if (solution.getState() != Optimisation.State.OPTIMAL
          && solution.getState() != Optimisation.State.FEASIBLE) {
      throw new IllegalStateException("Optimisation failed: " + solution.getState());
    }

    return new Result(
      readVar(soc, n + 1),
      readVar(charge, n),
      readVar(discharge, n),
      readVar(gridIn, n),
      readVar(gridOut, n),
      readVar(pvUsed, n),
      solution.getValue()
    );
  }

  private static List<Double> readVar(Variable[] vars, int n) {
    return java.util.stream.IntStream.range(0, n)
             .mapToObj(i -> vars[i].getValue().doubleValue())
             .toList();
  }

  private static double safeNonNeg(Double v) {
    if (v == null || Double.isNaN(v) || Double.isInfinite(v)) return 0.0;
    return Math.max(0.0, v);
  }

  private static BigDecimal bd(double v) {
    if (Double.isNaN(v) || Double.isInfinite(v)) return BigDecimal.ZERO;
    return BigDecimal.valueOf(v);
  }

  private Task11BatteryOptimizer() {
  }
}
