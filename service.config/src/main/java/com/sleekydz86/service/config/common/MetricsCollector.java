package com.sleekydz86.service.config.common;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MetricsCollector {

    private final MeterRegistry meterRegistry;

    public MetricsCollector(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public Map<String, Object> collectCustomMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        addMetric(metrics, "requestCount", "http.requests.count", MetricType.COUNTER);

        return metrics;
    }

    private void addMetric(Map<String, Object> metrics, String key, String metricName, MetricType type) {
        try {
            double value = switch (type) {
                case GAUGE -> meterRegistry.get(metricName).gauge().value();
                case COUNTER -> meterRegistry.get(metricName).counter().count();
            };
            metrics.put(key, value);
        } catch (Exception e) {
            metrics.put(key, type == MetricType.COUNTER ? 0.0 : 0.0);
        }
    }

    private enum MetricType {
        GAUGE, COUNTER
    }
}

