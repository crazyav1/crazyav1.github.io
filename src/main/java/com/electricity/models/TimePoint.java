package com.electricity.models;

public record TimePoint(
  String ts,   // ISO timestamp
  double value
) {}
