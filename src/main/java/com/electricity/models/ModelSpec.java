package com.electricity.models;

import com.electricity.util.Task10Forecaster;

public record ModelSpec(String name, Task10Forecaster forecaster) {
  public static ModelSpec of(String name, Task10Forecaster f) {
    return new ModelSpec(name, f);
  }
}
