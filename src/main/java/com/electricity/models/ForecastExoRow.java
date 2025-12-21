package com.electricity.models;

import java.time.OffsetDateTime;

public record ForecastExoRow(
  OffsetDateTime timestamp,
  double demandActual,
  double temperature,
  double cloudCover,
  double windSpeed10m,
  double pressure
) {}
