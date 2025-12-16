package com.sleekydz86.service.healthcare.service.chart;

import java.util.Map;

public interface ChartDataProcessor {
    Map<String, Object> processChartData(Map<String, Object> rawData);
}

