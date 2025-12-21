package com.electricity.models;

import java.time.OffsetDateTime;

public record ForecastRow(OffsetDateTime timestamp, double demandActual) {}
