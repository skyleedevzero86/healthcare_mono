package com.sleekydz86.service.healthcare.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/actuator")
public class HealthController {

    @Autowired
    private HealthIndicator healthIndicator;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private DataSource dataSource;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = new HashMap<>();

        boolean dbHealthy = checkDatabaseHealth();
        health.put("database", dbHealthy ? "UP" : "DOWN");

        boolean cacheHealthy = checkCacheHealth();
        health.put("cache", cacheHealthy ? "UP" : "DOWN");

        boolean overallHealthy = dbHealthy && cacheHealthy;
        health.put("status", overallHealthy ? "UP" : "DOWN");
        health.put("timestamp", Instant.now().toString());

        return ResponseEntity.status(overallHealthy ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE)
            .body(health);
    }

    private boolean checkDatabaseHealth() {
        try {
            try (Connection connection = dataSource.getConnection()) {
                return connection.isValid(1);
            }
        } catch (Exception e) {
            return false;
        }
    }

    private boolean checkCacheHealth() {
        try {
            redisTemplate.opsForValue().get("health-check");
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

