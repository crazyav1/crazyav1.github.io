package com.electricity.models;

import java.time.OffsetDateTime;

public record DemandForecastRow(
  OffsetDateTime timestamp,
  double demandForecast
) {}
