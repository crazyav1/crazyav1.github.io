package com.electricity.util;

import com.electricity.report.ForecastReport;

public final class ReportPrinter {

  public static void printOverallTSV(ForecastReport report) {
    System.out.println("Model\tNRMSE");
    report.overall().forEach(r ->
                               System.out.printf("%s\t%.6f%n", r.model(), r.nrmse())
    );
  }

  public static void printPerDayTSV(ForecastReport report) {
    var perDay = report.perDayNrmse();
    System.out.print("Day");
    perDay.keySet().forEach(m -> System.out.print("\t" + m));
    System.out.println();

    int days = report.days().size();
    for (int i = 0; i < days; i++) {
      System.out.print(report.days().get(i).day());
      for (var m : perDay.keySet()) {
        System.out.printf("\t%.6f", perDay.get(m).get(i));
      }
      System.out.println();
    }
  }

  private ReportPrinter() {}
}
