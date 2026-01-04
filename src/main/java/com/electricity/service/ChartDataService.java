package com.electricity.service;

import com.electricity.dto.DatasetDto;
import com.electricity.dto.PointDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class ChartDataService {

    public List<PointDto> getTimeSeries(Instant from, Instant to, Duration step) {
        List<PointDto> list = new ArrayList<>();
        long start = from.toEpochMilli();
        long end = to.toEpochMilli();
        long stepMs = step.toMillis();
        for (long t = start; t <= end; t += stepMs) {
            double v = 100 + 20 * Math.sin((t - start) / (1000.0 * 60 * 60 * 24) * 2 * Math.PI) + (Math.random() - 0.5) * 10;
            list.add(new PointDto(t, v));
        }
        return list;
    }

    public List<PointDto> getPriceSeries(Instant from, Instant to, Duration step) {
        List<PointDto> list = new ArrayList<>();
        long start = from.toEpochMilli();
        long end = to.toEpochMilli();
        long stepMs = step.toMillis();
        for (long t = start; t <= end; t += stepMs) {
            double v = 30 + 5 * Math.cos((t - start) / (1000.0 * 60 * 60) * 2 * Math.PI);
            list.add(new PointDto(t, v));
        }
        return list;
    }

    public List<DatasetDto> getMultiSeries(Instant from, Instant to, Duration step) {
        List<DatasetDto> datasets = new ArrayList<>();
        datasets.add(new DatasetDto("Demand", getTimeSeries(from, to, step), "left", "kW"));
        datasets.add(new DatasetDto("Price", getPriceSeries(from, to, step), "right", "EUR/MWh"));
        return datasets;
    }
}

