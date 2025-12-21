package com.electricity.config;

import java.nio.file.Path;

public class Constants {
  public static final Path TRAIN_PATH = Path.of("./src/main/resources/train_211628.csv");
  public static final Path OPTIMIZATION_PATH = Path.of("./src/main/resources/optimisation.csv");
  public static final Path FORECAST_PATH = Path.of("./src/main/resources/forecast.csv");

  public static final Path OUT_DIR = Path.of("./out/figures");

  //task 4
  public static final double QUANTILE = 0.90;

  //Task 11
  public static final int HORIZON_HOURS = 24;

  public static final Path PV_LOW_SCHEDULE_PATH = Path.of("./out/task11_schedule_pv_low.csv");
  public static final Path PV_HIGH_SCHEDULE_PATH = Path.of("./out/task11_schedule_pv_high.csv");

  private Constants() {
  }
}
