package com.electricity.models;

import java.time.OffsetDateTime;

public record OptimisationRow(
  OffsetDateTime timestamp,
  double pvLow,
  double pvHigh,
  double price
) {}
