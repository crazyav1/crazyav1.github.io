package com.electricity.dto;

import lombok.Value;
import java.util.List;

@Value
public class DatasetDto {
    String label;
    List<PointDto> points;
    String yAxisId; // e.g., "left" or "right"
    String unit;
}

