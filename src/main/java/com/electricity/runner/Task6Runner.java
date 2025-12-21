package com.electricity.runner;

import com.electricity.config.OutputConfig;
import com.electricity.plotter.DecompositionPlotter;
import com.electricity.plotter.TypicalProfilesPlotter;
import com.electricity.reader.CsvReader;
import com.electricity.util.*;

import static com.electricity.config.Constants.OUT_DIR;
import static com.electricity.config.Constants.TRAIN_PATH;

public final class Task6Runner {

  private static final OutputConfig OUT = OutputConfig.defaults(OUT_DIR);

  static void main() throws Exception {
    var rows = CsvReader.loadTrainTest(TRAIN_PATH);
    var dec = ClassicalDecomposition.decompose(rows, ClassicalDecomposition.Settings.defaults());

    DecompositionPlotter.plot(dec, rows, OUT);

    var strength = SeasonalStrength.byMonth(dec, rows);

    System.out.println("SEASONAL EFFECT STRENGTH BY MONTH");
    System.out.println("Month\tStrength");
    strength.forEach((m, v) -> System.out.printf("%s\t%.6f%n", m, v));

    var profiles = TypicalProfiles.compute(rows);

    System.out.println("TYPICAL DEMAND PROFILES");
    TypicalProfiles.printTSV(profiles);

    TypicalProfilesPlotter.plot(profiles, OUT);
  }

  private Task6Runner() {}
}
