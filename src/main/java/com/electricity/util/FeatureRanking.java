package com.electricity.util;

import java.util.Comparator;
import java.util.List;

public final class FeatureRanking {

  public record RankedFeature(String name, double corr, double absCorr, int nPairs) {}

  public static List<RankedFeature> rankByAbsPearson(FeatureEngineering.Dataset ds) {
    return ds.features().stream()
             .map(f -> {
               var r = Correlation.pearson(ds.y(), f.values());
               return new RankedFeature(f.name(), r.value(), Math.abs(r.value()), r.nPairs());
             })
             .sorted(Comparator.comparingDouble(RankedFeature::absCorr).reversed())
             .toList();
  }

  public static void printTSV(List<RankedFeature> ranking) {
    System.out.println("Feature\tPearson_r\tAbs_r\tN_pairs");
    ranking.forEach(rf ->
                      System.out.printf("%s\t%.6f\t%.6f\t%d%n", rf.name(), rf.corr(), rf.absCorr(), rf.nPairs()));
  }

  private FeatureRanking() {}
}
