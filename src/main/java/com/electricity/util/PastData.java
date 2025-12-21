package com.electricity.util;

import com.electricity.models.DataRow;
import com.electricity.models.PastRow;

import java.util.ArrayList;
import java.util.List;

public final class PastData {

  public static List<PastRow> fromTrainRows(List<DataRow> rows) {
    var out = new ArrayList<PastRow>(rows.size());
    for (var r : rows) {
      out.add(new PastRow(
        r.timestamp(),
        r.demand(),
        r.temperature(),
        r.cloudCover(),
        r.windSpeed10m(),
        r.pressure()
      ));
    }
    return out;
  }

  private PastData() {}
}
