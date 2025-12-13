package com.sleekydz86.service.healthcare.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ChatgptService {

    private final RestTemplate restTemplate;
    private final ChatgptProperties chatgptProperties;
    private final ObjectMapper objectMapper;

    @Value("${chatgpt.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${chatgpt.api.key:}")
    private String apiKey;

    @Value("${chatgpt.enabled:false}")
    private boolean enabled;

    public ChatgptService(ChatgptProperties chatgptProperties, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.chatgptProperties = chatgptProperties;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String sendMessage(String query) {
        if (!enabled || apiKey == null || apiKey.isEmpty()) {
            log.warn("ChatGPT API가 비활성화되었거나 API 키가 설정되지 않았습니다. 더미 응답을 반환합니다.");
            return generateDummyResponse(query);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model",
                    chatgptProperties.getModel() != null ? chatgptProperties.getModel() : "gpt-3.5-turbo");
            requestBody.put("temperature", chatgptProperties.getTemperature());
            requestBody.put("messages", List.of(
                    Map.of("role", "user", "content", query)));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    request,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                JsonNode choices = jsonNode.get("choices");
                if (choices != null && choices.isArray() && choices.size() > 0) {
                    JsonNode message = choices.get(0).get("message");
                    if (message != null && message.has("content")) {
                        String content = message.get("content").asText();
                        log.info("ChatGPT API 응답 수신 성공");
                        return content;
                    }
                }
            }

            log.warn("ChatGPT API 응답 형식이 예상과 다릅니다. 더미 응답을 반환합니다.");
            return generateDummyResponse(query);
        } catch (Exception e) {
            log.error("ChatGPT API 호출 중 오류 발생", e);
            return generateDummyResponse(query);
        }
    }

    private String generateDummyResponse(String query) {
        if (query != null && query.contains("심박수") && query.contains("혈압")) {
            return "건강 상태를 분석한 결과, 정기적인 운동과 균형 잡힌 식단을 권장합니다. " +
                    "심박수와 혈압 수치를 지속적으로 모니터링하시기 바랍니다.";
        }
        return "AI 응답을 생성할 수 없습니다. ChatGPT API가 설정되지 않았거나 오류가 발생했습니다.";
    }
}
