package com.electricity.config;

public record BatterySpec(
  double capacityKWh,
  double initialSocKWh,
  double minSocKWh,
  double maxSocKWh,
  double chargeLimitKW,
  double dischargeLimitKW,
  double etaCharge,
  double etaDischarge
) {
  public static BatterySpec defaults() {
    var capacity = 10.0; // 10 kWh home battery
    var initial = 5.0;
    return new BatterySpec(
      capacity,
      initial,
      0.5,          // reserve
      capacity,
      3.0,          // kW charge limit
      3.0,          // kW discharge limit
      0.95,
      0.95
    );
  }
}
