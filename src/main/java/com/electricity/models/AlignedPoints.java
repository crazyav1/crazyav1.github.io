package com.electricity.models;

import java.util.Date;
import java.util.List;

public record AlignedPoints(
  List<Date> time,
  List<Double> demand,
  List<Double> pv,
  List<Double> price
) {
}