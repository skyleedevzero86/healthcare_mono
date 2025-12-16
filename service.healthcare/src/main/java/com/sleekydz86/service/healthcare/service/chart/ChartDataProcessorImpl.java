package com.sleekydz86.service.healthcare.service.chart;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ChartDataProcessorImpl implements ChartDataProcessor {
    @Override
    public Map<String, Object> processChartData(Map<String, Object> rawData) {
        return rawData;
    }
}

