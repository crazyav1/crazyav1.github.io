package com.electricity.models;

import java.util.List;
import java.util.Map;

public record SeriesResponse(
  List<String> labels,
  Map<String, List<Double>> series
) {}