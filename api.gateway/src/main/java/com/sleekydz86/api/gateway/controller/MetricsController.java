package com.sleekydz86.api.gateway.controller;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/actuator/metrics")
public class MetricsController {

    @Autowired
    private MeterRegistry meterRegistry;

    @GetMapping("/custom")
    public ResponseEntity<Map<String, Object>> customMetrics() {
        Map<String, Object> metrics = new HashMap<>();

        try {
            double cacheHitRate = meterRegistry.get("cache.hit.rate").gauge().value();
            metrics.put("cacheHitRate", cacheHitRate);
        } catch (Exception e) {
            metrics.put("cacheHitRate", 0.0);
        }

        try {
            double requestCount = meterRegistry.get("http.requests.count").counter().count();
            metrics.put("requestCount", requestCount);
        } catch (Exception e) {
            metrics.put("requestCount", 0.0);
        }

        return ResponseEntity.ok(metrics);
    }
}

