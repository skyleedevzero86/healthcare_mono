package com.sleekydz86.service.llm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.llm.config.LLMProperties;
import com.sleekydz86.service.llm.dto.LLMRequest;
import com.sleekydz86.service.llm.dto.LLMResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class LLMServiceImpl implements LLMService {

    private final RestTemplate restTemplate;
    private final WebClient webClient;
    private final LLMProperties llmProperties;
    private final ObjectMapper objectMapper;
    private final PromptTemplateService promptTemplateService;
    private final ConversationService conversationService;
    private final CacheService cacheService;

    @Override
    public LLMResponse generate(LLMRequest request) {
        long startTime = System.currentTimeMillis();

        try {
            String finalPrompt = buildFinalPrompt(request);

            LLMResponse cachedResponse = cacheService.getCachedResponse(finalPrompt);
            if (cachedResponse != null) {
                log.debug("캐시된 응답 반환");
                cachedResponse.setProcessingTimeMs(System.currentTimeMillis() - startTime);
                return cachedResponse;
            }

            Map<String, Object> requestBody = buildLlamaCppRequest(finalPrompt, request);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);

            String serverUrl = llmProperties.getLlamaCpp().getServerUrl() + "/completion";

            log.debug("LLM 요청 전송: URL={}, Prompt length={}", serverUrl, finalPrompt.length());

            ResponseEntity<String> response = restTemplate.exchange(
                    serverUrl,
                    HttpMethod.POST,
                    httpEntity,
                    String.class);

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                log.error("LLM 서버 응답 오류: status={}", response.getStatusCode());
                throw new RuntimeException("LLM 서버 응답 오류");
            }

            LLMResponse llmResponse = parseLlamaCppResponse(response.getBody(), request);
            llmResponse.setProcessingTimeMs(System.currentTimeMillis() - startTime);
            llmResponse.setTimestamp(LocalDateTime.now());

            if (request.getConversationId() != null) {
                conversationService.saveMessage(
                        request.getConversationId(),
                        "user",
                        finalPrompt);
                conversationService.saveMessage(
                        request.getConversationId(),
                        "assistant",
                        llmResponse.getResponse());
            }

            cacheService.cacheResponse(finalPrompt, llmResponse);

            log.info("LLM 응답 생성 완료: tokens={}, time={}ms",
                    llmResponse.getTokensUsed(), llmResponse.getProcessingTimeMs());

            return llmResponse;

        } catch (RestClientException e) {
            log.error("LLM 서버 통신 오류", e);
            throw new RuntimeException("LLM 서비스 통신 오류: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("LLM 응답 생성 중 오류", e);
            throw new RuntimeException("LLM 응답 생성 오류: " + e.getMessage(), e);
        }
    }

    @Override
    public void generateStream(LLMRequest request, java.util.function.Consumer<String> onChunk) {
        try {
            String finalPrompt = buildFinalPrompt(request);
            Map<String, Object> requestBody = buildLlamaCppRequest(finalPrompt, request);
            requestBody.put("stream", true);

            String serverUrl = llmProperties.getLlamaCpp().getServerUrl() + "/completion";

            Flux<String> responseFlux = webClient.post()
                    .uri(serverUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToFlux(String.class)
                    .timeout(Duration.ofSeconds(llmProperties.getLlamaCpp().getTimeout() / 1000));

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
    public LLMResponse generateWithHistory(LLMRequest request) {
        if (request.getConversationId() != null) {
            List<LLMResponse.Message> history = conversationService.getHistory(request.getConversationId());
            if (history != null && !history.isEmpty()) {
                StringBuilder contextPrompt = new StringBuilder();
                for (LLMResponse.Message msg : history) {
                    contextPrompt.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n\n");
                }
                contextPrompt.append("user: ").append(request.getPrompt());

                LLMRequest requestWithHistory = LLMRequest.builder()
                        .prompt(contextPrompt.toString())
                        .template(request.getTemplate())
                        .variables(request.getVariables())
                        .userId(request.getUserId())
                        .conversationId(request.getConversationId())
                        .maxTokens(request.getMaxTokens())
                        .temperature(request.getTemperature())
                        .build();

                return generate(requestWithHistory);
            }
        }

        return generate(request);
    }

    @Override
    public boolean isAvailable() {
        try {
            String healthUrl = llmProperties.getLlamaCpp().getServerUrl() + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(healthUrl, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("LLM 서비스 상태 확인 실패", e);
            return false;
        }
    }

    private String buildFinalPrompt(LLMRequest request) {
        if (request.getTemplate() != null && !request.getTemplate().isEmpty()) {
            if ("healthcare".equals(request.getTemplate()) && request.getVariables() != null) {
                return promptTemplateService.buildHealthcarePromptFromMap(request.getVariables());
            } else if (request.getVariables() != null) {
                return promptTemplateService.buildGeneralPrompt(request.getPrompt(), request.getVariables());
            }
        }

        return request.getPrompt();
    }

    private Map<String, Object> buildLlamaCppRequest(String prompt, LLMRequest request) {
        Map<String, Object> body = new HashMap<>();

        String systemPrompt = request.getSystemPrompt();
        if (systemPrompt == null && request.getTemplate() != null) {
            systemPrompt = promptTemplateService.getSystemPrompt(request.getTemplate());
        }

        if (systemPrompt != null) {
            body.put("system_prompt", systemPrompt);
        }

        body.put("prompt", prompt);
        body.put("n_predict",
                request.getMaxTokens() != null ? request.getMaxTokens() : llmProperties.getModel().getMaxTokens());
        body.put("temperature", request.getTemperature() != null ? request.getTemperature()
                : llmProperties.getModel().getTemperature());
        body.put("top_p", request.getTopP() != null ? request.getTopP() : llmProperties.getModel().getTopP());
        body.put("top_k", request.getTopK() != null ? request.getTopK() : llmProperties.getModel().getTopK());
        body.put("repeat_penalty", llmProperties.getModel().getRepeatPenalty());
        body.put("stop", List.of("<|im_end|>", "<|endoftext|>", "\n\n\n"));

        return body;
    }

    private LLMResponse parseLlamaCppResponse(String responseBody, LLMRequest request) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);

            String content = root.has("content") ? root.get("content").asText() : "";
            int tokensUsed = root.has("tokens_predicted") ? root.get("tokens_predicted").asInt() : 0;
            int promptTokens = root.has("tokens_evaluated") ? root.get("tokens_evaluated").asInt() : 0;

            LLMResponse response = LLMResponse.builder()
                    .response(content.trim())
                    .conversationId(request.getConversationId())
                    .tokensUsed(tokensUsed)
                    .promptTokens(promptTokens)
                    .completionTokens(tokensUsed - promptTokens)
                    .build();

            Map<String, Object> metadata = new HashMap<>();
            if (root.has("model")) {
                metadata.put("model", root.get("model").asText());
            }
            if (root.has("generation_settings")) {
                metadata.put("generation_settings", root.get("generation_settings"));
            }
            response.setMetadata(metadata);

            return response;

        } catch (Exception e) {
            log.error("LLM 응답 파싱 오류", e);
            throw new RuntimeException("응답 파싱 오류: " + e.getMessage(), e);
        }
    }
}
