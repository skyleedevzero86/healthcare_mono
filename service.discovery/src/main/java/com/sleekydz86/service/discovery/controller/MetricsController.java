package com.sleekydz86.service.discovery.controller;

import com.sleekydz86.service.discovery.common.MetricsCollector;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/actuator/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsCollector metricsCollector;

    @GetMapping("/custom")
    public ResponseEntity<Map<String, Object>> customMetrics() {
        return ResponseEntity.ok(metricsCollector.collectCustomMetrics());
    }
}

