package com.electricity.config;

public record BatterySpec(
  double initialSocKWh,
  double minSocKWh,
  double maxSocKWh,
  double chargeLimitKW,
  double dischargeLimitKW,
  double etaCharge,
  double etaDischarge
) {
}
