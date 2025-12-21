package com.electricity.util;

import com.electricity.report.Task10Report;

public final class Task10ReportPrinter {

  public static void printOverallTSV(Task10Report report) {
    System.out.println("Model\tMAE\tNRMSE");
    for (var o : report.overall()) {
      System.out.printf("%s\t%.6f\t%.6f%n", o.model(), o.mae(), o.nrmse());
    }
  }

  public static void printImprovementsTSV(Task10Report report, String arBaseline, String xgbBaseline) {
    var impArx = report.improvement(arBaseline, "ARX(24)+exo");
    var impXgb = report.improvement(xgbBaseline, "XGBoost(lags+exo)");

    System.out.println("ImprovedModel\tBaseline\tMAE_Improvement_%\tNRMSE_Improvement_%");

    System.out.printf("%s\t%s\t%.2f\t%.2f%n",
      impArx.improved(), impArx.baseline(),
      impArx.maeImprovementPct(), impArx.nrmseImprovementPct()
    );

    System.out.printf("%s\t%s\t%.2f\t%.2f%n",
      impXgb.improved(), impXgb.baseline(),
      impXgb.maeImprovementPct(), impXgb.nrmseImprovementPct()
    );
  }

  private Task10ReportPrinter() {}
}
