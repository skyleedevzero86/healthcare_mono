package com.sleekydz86.api.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/actuator")
public class HealthController {

    @Autowired
    private ReactiveRedisConnectionFactory redisConnectionFactory;

    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> health() {
        Map<String, Object> health = new HashMap<>();

        boolean cacheHealthy = checkCacheHealth();
        health.put("cache", cacheHealthy ? "UP" : "DOWN");

        boolean overallHealthy = cacheHealthy;
        health.put("status", overallHealthy ? "UP" : "DOWN");
        health.put("timestamp", Instant.now().toString());

        return Mono.just(ResponseEntity.status(overallHealthy ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE)
                .body(health));
    }

    private boolean checkCacheHealth() {
        try {
            redisConnectionFactory.getReactiveConnection().ping().block();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
