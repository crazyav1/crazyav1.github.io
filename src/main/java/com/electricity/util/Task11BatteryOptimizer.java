package com.electricity.util;

import com.electricity.config.BatterySpec;
import com.electricity.config.Task11Config;
import com.electricity.models.DemandForecastRow;
import com.electricity.models.OptimisationRow;
import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

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
    Task11Config config,
    Function<OptimisationRow, Double> pvSelector
  ) {

    if (optRows.size() != demandForecast.size()) {
      throw new IllegalArgumentException("optRows and demandForecast must have same size (24 hours).");
    }
    int n = optRows.size();
    var battery = config.battery();

    var model = new ExpressionsBasedModel();

    var soc = new Variable[n + 1];
    var charge = new Variable[n];
    var discharge = new Variable[n];
    var gridIn = new Variable[n];
    var gridOut = new Variable[n];
    var pvUsed = new Variable[n];

    Expression obj = model.addExpression("objective").weight(ONE);

    for (int t = 0; t <= n; t++) {
      soc[t] = model.addVariable("soc_" + t)
                 .lower(bd(battery.minSocKWh()))
                 .upper(bd(battery.maxSocKWh()));
    }

    model.addExpression("soc_init")
      .level(bd(battery.initialSocKWh()))
      .set(soc[0], ONE);

    for (int t = 0; t < n; t++) {
      charge[t] = model.addVariable("ch_" + t)
                    .lower(ZERO)
                    .upper(bd(battery.chargeLimitKW()));

      discharge[t] = model.addVariable("dis_" + t)
                       .lower(ZERO)
                       .upper(bd(battery.dischargeLimitKW()));

      gridIn[t] = model.addVariable("gridIn_" + t)
                    .lower(ZERO)
                    .upper(bd(config.gridLimitKW()));

      gridOut[t] = model.addVariable("gridOut_" + t)
                     .lower(ZERO)
                     .upper(bd(config.gridLimitKW()));

      pvUsed[t] = model.addVariable("pvUsed_" + t)
                    .lower(ZERO);

      double pvAvail = safeNonNeg(pvSelector.apply(optRows.get(t)));
      model.addExpression("pv_cap_" + t)
        .upper(bd(pvAvail))
        .set(pvUsed[t], ONE);

      model.addExpression("soc_dyn_" + t)
        .level(ZERO)
        .set(soc[t + 1], ONE)
        .set(soc[t], ONE.negate())
        .set(charge[t], bd(-battery.etaCharge()))
        .set(discharge[t], bd(1.0 / battery.etaDischarge()));

      double demand = demandForecast.get(t).demandForecast();
      model.addExpression("balance_" + t)
        .level(bd(demand))
        .set(gridIn[t], ONE)
        .set(pvUsed[t], ONE)
        .set(discharge[t], ONE)
        .set(charge[t], ONE.negate())
        .set(gridOut[t], ONE.negate());

      double price = safeNonNeg(optRows.get(t).price());
      obj.set(gridIn[t], bd(price));
      obj.set(gridOut[t], bd(-config.exportPriceFactor() * price));
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
    if (Double.isNaN(v) || Double.isInfinite(v)) return ZERO;
    return BigDecimal.valueOf(v);
  }

  private Task11BatteryOptimizer() {
  }
}
