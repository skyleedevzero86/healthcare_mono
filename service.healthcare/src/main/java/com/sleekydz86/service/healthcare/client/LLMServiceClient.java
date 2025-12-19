package com.sleekydz86.service.healthcare.client;

import com.sleekydz86.service.healthcare.common.ServiceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(
    name = "service.llm",
    path = "/api/llm",
    fallback = LLMServiceClientFallback.class
)
public interface LLMServiceClient {
    
    @PostMapping("/chat_ai")
    Map<String, Object> chatAi(@RequestBody Map<String, Object> request);
}

