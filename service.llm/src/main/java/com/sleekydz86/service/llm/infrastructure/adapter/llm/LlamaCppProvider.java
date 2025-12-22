package com.sleekydz86.service.llm.infrastructure.adapter.llm;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.llm.config.LlamaCppProperties;
import com.sleekydz86.service.llm.config.ModelProperties;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class LlamaCppProvider implements LLMProvider {

    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final LlamaCppProperties llamaCppProperties;
    private final ModelProperties modelProperties;
    private final ObjectMapper objectMapper;

    @Override
    public LLMGenerationResult generate(LLMGenerationRequest request) {
        try {
            Prompt prompt = request.getPrompt();
            Map<String, Object> requestBody = buildLlamaCppRequest(prompt, request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);
            String serverUrl = llamaCppProperties.getServerUrl() + "/completion";

            log.debug("LLM 요청 전송: URL={}, Prompt length={}", serverUrl, prompt.getFinalContent().length());

            ResponseEntity<String> response = restTemplate.exchange(
                    serverUrl,
                    HttpMethod.POST,
                    httpEntity,
                    String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("LLM 서버 응답 오류: status={}", response.getStatusCode());
                throw new RuntimeException("LLM 서버 응답 오류");
            }

            return parseLlamaCppResponse(response.getBody());

        } catch (RestClientException e) {
            log.error("LLM 서버 통신 오류", e);
            throw new RuntimeException("LLM 서비스 통신 오류: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("LLM 응답 생성 중 오류", e);
            throw new RuntimeException("LLM 응답 생성 오류: " + e.getMessage(), e);
        }
    }

    @Override
    public void generateStream(LLMGenerationRequest request, Consumer<String> onChunk) {
        try {
            Prompt prompt = request.getPrompt();
            Map<String, Object> requestBody = buildLlamaCppRequest(prompt, request);
            requestBody.put("stream", true);

            String serverUrl = llamaCppProperties.getServerUrl() + "/completion";

            Flux<String> responseFlux = webClient.post()
                    .uri(serverUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .timeout(Duration.ofSeconds(llamaCppProperties.getTimeout() / 1000));

            responseFlux.subscribe(
                    chunk -> {
                        try {
                            if (chunk.startsWith("data: ")) {
                                String json = chunk.substring(6);
                                if (!json.equals("[DONE]")) {
                                    JsonNode node = objectMapper.readTree(json);
                                    if (node.has("content")) {
                                        onChunk.accept(node.get("content").asText());
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
        try {
            String healthUrl = llamaCppProperties.getServerUrl() + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(healthUrl, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("LLM 서비스 상태 확인 실패", e);
            return false;
        }
    }

    private Map<String, Object> buildLlamaCppRequest(Prompt prompt, LLMGenerationRequest request) {
        Map<String, Object> body = new HashMap<>();

        String systemPrompt = prompt.getSystemPrompt();
        if (systemPrompt != null) {
            body.put("system_prompt", systemPrompt);
        }

        body.put("prompt", prompt.getFinalContent());
        body.put("n_predict", request.getMaxTokens() != null 
                ? request.getMaxTokens() 
                : modelProperties.getMaxTokens());
        body.put("temperature", request.getTemperature() != null 
                ? request.getTemperature() 
                : modelProperties.getTemperature());
        body.put("top_p", request.getTopP() != null 
                ? request.getTopP() 
                : modelProperties.getTopP());
        body.put("top_k", request.getTopK() != null 
                ? request.getTopK() 
                : modelProperties.getTopK());
        body.put("repeat_penalty", modelProperties.getRepeatPenalty());
        body.put("stop", List.of("<|im_end|>", "<|endoftext|>", "\n\n\n"));

        return body;
    }

    private LLMGenerationResult parseLlamaCppResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            String content = root.has("content") ? root.get("content").asText() : "";
            int tokensUsed = root.has("tokens_predicted") ? root.get("tokens_predicted").asInt() : 0;
            int promptTokens = root.has("tokens_evaluated") ? root.get("tokens_evaluated").asInt() : 0;

            Map<String, Object> metadata = new HashMap<>();
            if (root.has("model")) {
                metadata.put("model", root.get("model").asText());
            }
            if (root.has("generation_settings")) {
                metadata.put("generation_settings", root.get("generation_settings"));
            }

            return LLMGenerationResult.builder()
                    .content(content.trim())
                    .tokensUsed(tokensUsed)
                    .promptTokens(promptTokens)
                    .completionTokens(tokensUsed - promptTokens)
                    .timestamp(LocalDateTime.now())
                    .metadata(metadata)
                    .build();

        } catch (Exception e) {
            log.error("LLM 응답 파싱 오류", e);
            throw new RuntimeException("응답 파싱 오류: " + e.getMessage(), e);
        }
    }
}

