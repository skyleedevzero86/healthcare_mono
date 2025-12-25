package com.sleekydz86.service.healthcare.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class AuthServiceClientFallback implements AuthServiceClient {
    
    @Override
    public Map<String, Object> validateToken(Map<String, Object> request) {
        log.warn("AuthService 호출 실패 - Circuit Breaker 활성화. Fallback 응답 반환");
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("valid", false);
        fallbackResponse.put("error", "AuthService is temporarily unavailable");
        return fallbackResponse;
    }
    
    @Override
    public Map<String, Object> getUserInfo(Map<String, Object> request) {
        log.warn("AuthService 호출 실패 - Circuit Breaker 활성화. Fallback 응답 반환");
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("error", "AuthService is temporarily unavailable");
        return fallbackResponse;
    }

    @Override
    public Map<String, Object> getUserSeq(Map<String, String> request) {
        log.warn("AuthService 호출 실패 - Circuit Breaker 활성화. Fallback 응답 반환");
        Map<String, Object> fallbackResponse = new HashMap<>();
        fallbackResponse.put("error", "AuthService is temporarily unavailable");
        return fallbackResponse;
    }
}

