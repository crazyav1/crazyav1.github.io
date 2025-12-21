package com.electricity.models;

import java.time.OffsetDateTime;

public record PastRow(
  OffsetDateTime timestamp,
  double demand,
  double temperature,
  double cloudCover,
  double windSpeed10m,
  double pressure
) {}
