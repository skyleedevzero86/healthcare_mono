package com.sleekydz86.service.healthcare.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class LLMServiceClientFallback implements LLMServiceClient {

    @Override
    public Map<String, Object> chatAi(Map<String, Object> request) {
        log.warn("LLM 서비스 호출 실패. Fallback 응답 반환");
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("code", "LLM_SERVICE_UNAVAILABLE");
        result.put("message", "LLM 서비스가 현재 사용할 수 없습니다.");
        
        Map<String, Object> data = new HashMap<>();
        data.put("aiResponse", "AI 서비스가 일시적으로 사용할 수 없습니다. 잠시 후 다시 시도해주세요.");
        result.put("data", data);
        
        return result;
    }
}

