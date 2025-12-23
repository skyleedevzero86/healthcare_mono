package com.sleekydz86.service.llm.infrastructure.adapter.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.llm.config.Glm47Properties;
import com.sleekydz86.service.llm.domain.model.LLMGenerationRequest;
import com.sleekydz86.service.llm.domain.model.LLMGenerationResult;
import com.sleekydz86.service.llm.domain.model.Prompt;
import com.sleekydz86.service.llm.ports.outbound.LLMProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Component("glm47Provider")
@RequiredArgsConstructor
public class Glm47Provider implements LLMProvider {

    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final Glm47Properties glm47Properties;
    private final ObjectMapper objectMapper;

    @Override
    public LLMGenerationResult generate(LLMGenerationRequest request) {
        try {
            Prompt prompt = request.getPrompt();
            Map<String, Object> requestBody = buildOpenAIRequest(prompt, request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(glm47Properties.getApiKey());

            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);
            String serverUrl = glm47Properties.getBaseUrl() + "/chat/completions";

            log.debug("GLM-4.7 요청 전송: URL={}, Prompt length={}", serverUrl, prompt.getFinalContent().length());

            ResponseEntity<String> response = restTemplate.exchange(
                    serverUrl,
                    HttpMethod.POST,
                    httpEntity,
                    String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("GLM-4.7 서버 응답 오류: status={}", response.getStatusCode());
                throw new RuntimeException("GLM-4.7 서버 응답 오류");
            }

            return parseOpenAIResponse(response.getBody());

        } catch (RestClientException e) {
            log.error("GLM-4.7 서버 통신 오류", e);
            throw new RuntimeException("GLM-4.7 서비스 통신 오류: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("GLM-4.7 응답 생성 중 오류", e);
            throw new RuntimeException("GLM-4.7 응답 생성 오류: " + e.getMessage(), e);
        }
    }

    @Override
    public void generateStream(LLMGenerationRequest request, Consumer<String> onChunk) {
        try {
            Prompt prompt = request.getPrompt();
            Map<String, Object> requestBody = buildOpenAIRequest(prompt, request);
            requestBody.put("stream", true);

            String serverUrl = glm47Properties.getBaseUrl() + "/chat/completions";

            Flux<String> responseFlux = webClient.post()
                    .uri(serverUrl)
                    .header("Authorization", "Bearer " + glm47Properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .timeout(Duration.ofMillis(glm47Properties.getTimeout()));

            responseFlux.subscribe(
                    chunk -> {
                        try {
                            if (chunk.startsWith("data: ")) {
                                String json = chunk.substring(6);
                                if (!json.equals("[DONE]")) {
                                    JsonNode node = objectMapper.readTree(json);
                                    JsonNode choices = node.get("choices");
                                    if (choices != null && choices.isArray() && choices.size() > 0) {
                                        JsonNode delta = choices.get(0).get("delta");
                                        if (delta != null && delta.has("content")) {
                                            onChunk.accept(delta.get("content").asText());
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.warn("스트리밍 청크 파싱 오류", e);
                        }
                    },
                    error -> log.error("스트리밍 오류", error),
                    () -> log.debug("스트리밍 완료"));

        } catch (Exception e) {
            log.error("스트리밍 응답 생성 중 오류", e);
            throw new RuntimeException("스트리밍 응답 생성 오류: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isAvailable() {
        if (!glm47Properties.isEnabled() || glm47Properties.getApiKey() == null || glm47Properties.getApiKey().isEmpty()) {
            return false;
        }
        return true;
    }

    private Map<String, Object> buildOpenAIRequest(Prompt prompt, LLMGenerationRequest request) {
        Map<String, Object> body = new HashMap<>();
        
        List<Map<String, String>> messages = new ArrayList<>();
        
        if (prompt.getSystemPrompt() != null && !prompt.getSystemPrompt().isEmpty()) {
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", prompt.getSystemPrompt());
            messages.add(systemMessage);
        }
        
        Map<String, String> userMessage = new HashMap<>();
        userMessage.put("role", "user");
        userMessage.put("content", prompt.getFinalContent());
        messages.add(userMessage);
        
        body.put("model", glm47Properties.getModel());
        body.put("messages", messages);
        body.put("temperature", request.getTemperature() != null 
                ? request.getTemperature() 
                : glm47Properties.getChat().getTemperature());
        body.put("top_p", request.getTopP() != null 
                ? request.getTopP() 
                : glm47Properties.getChat().getTopP());
        body.put("max_tokens", request.getMaxTokens() != null 
                ? request.getMaxTokens() 
                : glm47Properties.getChat().getMaxTokens());
        
        if (glm47Properties.getThinking().isEnabled()) {
            Map<String, Object> extraBody = new HashMap<>();
            Map<String, Object> chatTemplateKwargs = new HashMap<>();
            chatTemplateKwargs.put("enable_thinking", true);
            chatTemplateKwargs.put("clear_thinking", !glm47Properties.getThinking().isPreserved());
            extraBody.put("chat_template_kwargs", chatTemplateKwargs);
            body.put("extra_body", extraBody);
        }
        
        return body;
    }

    private LLMGenerationResult parseOpenAIResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            JsonNode choices = root.get("choices");
            if (choices == null || !choices.isArray() || choices.size() == 0) {
                throw new RuntimeException("응답에 choices가 없습니다.");
            }

            JsonNode message = choices.get(0).get("message");
            String content = message != null && message.has("content") 
                    ? message.get("content").asText() 
                    : "";

            JsonNode usage = root.get("usage");
            int promptTokens = usage != null && usage.has("prompt_tokens") 
                    ? usage.get("prompt_tokens").asInt() 
                    : 0;
            int completionTokens = usage != null && usage.has("completion_tokens") 
                    ? usage.get("completion_tokens").asInt() 
                    : 0;
            int totalTokens = usage != null && usage.has("total_tokens") 
                    ? usage.get("total_tokens").asInt() 
                    : 0;

            Map<String, Object> metadata = new HashMap<>();
            if (root.has("model")) {
                metadata.put("model", root.get("model").asText());
            }
            if (root.has("id")) {
                metadata.put("id", root.get("id").asText());
            }

            return LLMGenerationResult.builder()
                    .content(content.trim())
                    .tokensUsed(totalTokens)
                    .promptTokens(promptTokens)
                    .completionTokens(completionTokens)
                    .timestamp(LocalDateTime.now())
                    .metadata(metadata)
                    .build();

        } catch (Exception e) {
            log.error("GLM-4.7 응답 파싱 오류", e);
            throw new RuntimeException("응답 파싱 오류: " + e.getMessage(), e);
        }
    }
}

