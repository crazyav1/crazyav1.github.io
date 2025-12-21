package com.electricity.util;

import com.electricity.models.ArDemandModel;
import com.electricity.models.DataRow;
import com.electricity.models.DemandForecastRow;
import com.electricity.models.OptimisationRow;

import java.util.ArrayList;
import java.util.List;

public final class DemandForecasterForTask11 {

  public static List<DemandForecastRow> forecast24(List<DataRow> train, List<OptimisationRow> opt24) {

    double[] y = train.stream().mapToDouble(DataRow::demand).toArray();

    var model = new ArDemandModel("AR(24) on diff", 24);
    double[] pred = model.forecastLevels(y, 24);

    var out = new ArrayList<DemandForecastRow>(24);
    for (int i = 0; i < 24; i++) {
      out.add(new DemandForecastRow(opt24.get(i).timestamp(), pred[i]));
    }
    return out;
  }

  private DemandForecasterForTask11() {}
}
