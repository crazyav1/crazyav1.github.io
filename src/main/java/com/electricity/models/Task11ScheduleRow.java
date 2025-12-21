package com.electricity.models;

import java.time.OffsetDateTime;

public record Task11ScheduleRow(
  OffsetDateTime timestamp,
  double price,
  double pvForecast,
  double demandForecast,
  double pvUsed,
  double gridImport,
  double gridExport,
  double charge,
  double discharge,
  double soc
) {}
