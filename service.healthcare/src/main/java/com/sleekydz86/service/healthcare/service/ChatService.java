package com.sleekydz86.service.healthcare.service;

import com.sleekydz86.service.healthcare.client.LLMServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final LLMServiceClient llmServiceClient;

    public String getChatResponse(Map<String, Object> requestMap) {
        try {
            log.debug("LLM 서비스 호출: userId={}", requestMap.get("userId"));

            Map<String, Object> response = llmServiceClient.chatAi(requestMap);

            if (response != null && response.containsKey("data")) {
                Object data = response.get("data");
                if (data instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> dataMap = (Map<String, Object>) data;
                    Object aiResponse = dataMap.get("aiResponse");
                    if (aiResponse != null) {
                        return aiResponse.toString();
                    }
                }
            }

            log.warn("LLM 서비스 응답에서 aiResponse를 찾을 수 없습니다.");
            return "AI 응답을 생성할 수 없습니다. 잠시 후 다시 시도해주세요.";

        } catch (Exception e) {
            log.error("LLM 서비스 호출 중 오류 발생", e);
            return "AI 서비스 호출 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }

    @Deprecated
    public String getChatResponse(String gptQuery) {
        log.warn("getChatResponse(String)는 deprecated되었습니다. Map 기반 메서드를 사용하세요.");

        Map<String, Object> requestMap = new HashMap<>();
        requestMap.put("prompt", gptQuery);
        return getChatResponse(requestMap);
    }
}
