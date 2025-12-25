package com.sleekydz86.api.gateway.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    
    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> authFallback() {
        log.warn("Auth Service Circuit Breaker 활성화 - Fallback 응답");
        Map<String, Object> response = new HashMap<>();
        response.put("resultCode", "5031");
        response.put("resultMessage", "인증 서비스가 일시적으로 사용할 수 없습니다. 잠시 후 다시 시도해주세요.");
        response.put("resultData", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/healthcare")
    public ResponseEntity<Map<String, Object>> healthcareFallback() {
        log.warn("Healthcare Service Circuit Breaker 활성화 - Fallback 응답");
        Map<String, Object> response = new HashMap<>();
        response.put("resultCode", "5031");
        response.put("resultMessage", "헬스케어 서비스가 일시적으로 사용할 수 없습니다. 잠시 후 다시 시도해주세요.");
        response.put("resultData", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/community")
    public ResponseEntity<Map<String, Object>> communityFallback() {
        log.warn("Community Service Circuit Breaker 활성화 - Fallback 응답");
        Map<String, Object> response = new HashMap<>();
        response.put("resultCode", "5031");
        response.put("resultMessage", "커뮤니티 서비스가 일시적으로 사용할 수 없습니다. 잠시 후 다시 시도해주세요.");
        response.put("resultData", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/management")
    public ResponseEntity<Map<String, Object>> managementFallback() {
        log.warn("User Management Service Circuit Breaker 활성화 - Fallback 응답");
        Map<String, Object> response = new HashMap<>();
        response.put("resultCode", "5031");
        response.put("resultMessage", "사용자 관리 서비스가 일시적으로 사용할 수 없습니다. 잠시 후 다시 시도해주세요.");
        response.put("resultData", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/comm")
    public ResponseEntity<Map<String, Object>> commFallback() {
        log.warn("Communication Service Circuit Breaker 활성화 - Fallback 응답");
        Map<String, Object> response = new HashMap<>();
        response.put("resultCode", "5031");
        response.put("resultMessage", "커뮤니케이션 서비스가 일시적으로 사용할 수 없습니다. 잠시 후 다시 시도해주세요.");
        response.put("resultData", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
    
    @GetMapping("/usermanagement")
    public ResponseEntity<Map<String, Object>> usermanagementFallback() {
        log.warn("User Management Service Circuit Breaker 활성화 - Fallback 응답");
        Map<String, Object> response = new HashMap<>();
        response.put("resultCode", "5031");
        response.put("resultMessage", "사용자 관리 서비스가 일시적으로 사용할 수 없습니다. 잠시 후 다시 시도해주세요.");
        response.put("resultData", null);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}

