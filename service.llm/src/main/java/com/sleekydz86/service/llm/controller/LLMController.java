package com.sleekydz86.service.llm.controller;

import com.sleekydz86.service.llm.application.mapper.LLMMapper;
import com.sleekydz86.service.llm.domain.model.ConversationId;
import com.sleekydz86.service.llm.domain.model.ConversationMessage;
import com.sleekydz86.service.llm.domain.model.LLMGenerationRequest;
import com.sleekydz86.service.llm.domain.model.Prompt;
import com.sleekydz86.service.llm.domain.service.PromptBuilder;
import com.sleekydz86.service.llm.dto.*;
import com.sleekydz86.service.llm.ports.inbound.GenerateLLMUseCase;
import com.sleekydz86.service.llm.ports.inbound.ManageConversationUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/llm")
@RequiredArgsConstructor
public class LLMController {

    private final GenerateLLMUseCase generateLLMUseCase;
    private final ManageConversationUseCase manageConversationUseCase;
    private final PromptBuilder promptBuilder;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<LLMResponse>> generate(@Valid @RequestBody LLMRequest request) {
        try {
            log.info("LLM 요청 수신: userId={}, template={}", request.getUserId(), request.getTemplate());

            if (!generateLLMUseCase.isAvailable()) {
                return ResponseEntity.ok(ApiResponse.error(
                        ApiResultCode.LLM_SERVICE_ERROR,
                        "LLM 서비스가 현재 사용할 수 없습니다."));
            }

            Prompt prompt = buildPrompt(request);
            LLMGenerationRequest domainRequest = LLMMapper.toDomain(request, prompt);
            var result = generateLLMUseCase.generate(domainRequest);

            if (request.getConversationId() != null) {
                ConversationId conversationId = ConversationId.of(request.getConversationId());
                manageConversationUseCase.saveMessage(
                        conversationId,
                        LLMMapper.toDomainMessage("user", prompt.getFinalContent()));
                manageConversationUseCase.saveMessage(
                        conversationId,
                        LLMMapper.toDomainMessage("assistant", result.getContent()));
            }

            LLMResponse response = LLMMapper.toDto(result, request.getConversationId());
            return ResponseEntity.ok(ApiResponse.ok(response));

        } catch (Exception e) {
            log.error("LLM 요청 처리 오류", e);
            return ResponseEntity.ok(ApiResponse.error(
                    ApiResultCode.LLM_SERVICE_ERROR,
                    "LLM 요청 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/healthcare")
    public ResponseEntity<ApiResponse<LLMResponse>> generateHealthcare(
            @Valid @RequestBody HealthcarePromptRequest request) {
        try {
            log.info("헬스케어 LLM 요청 수신: userId={}", request.getUserId());

            if (!generateLLMUseCase.isAvailable()) {
                return ResponseEntity.ok(ApiResponse.error(
                        ApiResultCode.LLM_SERVICE_ERROR,
                        "LLM 서비스가 현재 사용할 수 없습니다."));
            }

            Map<String, Object> bioInfo = convertToBioInfoMap(request);
            Prompt prompt = promptBuilder.buildHealthcarePrompt(bioInfo);

            LLMRequest llmRequest = LLMRequest.builder()
                    .prompt(prompt.getFinalContent())
                    .template("healthcare")
                    .userId(request.getUserId())
                    .build();

            LLMGenerationRequest domainRequest = LLMMapper.toDomain(llmRequest, prompt);
            var result = generateLLMUseCase.generate(domainRequest);

            LLMResponse response = LLMMapper.toDto(result, null);
            return ResponseEntity.ok(ApiResponse.ok(response));

        } catch (Exception e) {
            log.error("헬스케어 LLM 요청 처리 오류", e);
            return ResponseEntity.ok(ApiResponse.error(
                    ApiResultCode.LLM_SERVICE_ERROR,
                    "헬스케어 LLM 요청 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @PostMapping("/chat_ai")
    public ResponseEntity<ApiResponse<Map<String, Object>>> chatAi(@RequestBody Map<String, Object> request) {
        try {
            log.info("기존 형식 LLM 요청 수신");

            if (!generateLLMUseCase.isAvailable()) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("aiResponse", "LLM 서비스가 현재 사용할 수 없습니다.");
                return ResponseEntity.ok(ApiResponse.ok(errorResult));
            }

            Prompt prompt = promptBuilder.buildHealthcarePrompt(request);

            LLMRequest llmRequest = LLMRequest.builder()
                    .prompt(prompt.getFinalContent())
                    .template("healthcare")
                    .userId(request.get("userId") != null ? request.get("userId").toString() : null)
                    .build();

            LLMGenerationRequest domainRequest = LLMMapper.toDomain(llmRequest, prompt);
            var result = generateLLMUseCase.generate(domainRequest);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("aiResponse", result.getContent());

            return ResponseEntity.ok(ApiResponse.ok(responseMap));

        } catch (Exception e) {
            log.error("기존 형식 LLM 요청 처리 오류", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("aiResponse", "AI 응답 생성 중 오류가 발생했습니다.");
            return ResponseEntity.ok(ApiResponse.ok(errorResult));
        }
    }

    @PostMapping("/generate/with-history")
    public ResponseEntity<ApiResponse<LLMResponse>> generateWithHistory(@Valid @RequestBody LLMRequest request) {
        try {
            log.info("히스토리 포함 LLM 요청 수신: conversationId={}", request.getConversationId());

            if (!generateLLMUseCase.isAvailable()) {
                return ResponseEntity.ok(ApiResponse.error(
                        ApiResultCode.LLM_SERVICE_ERROR,
                        "LLM 서비스가 현재 사용할 수 없습니다."));
            }

            if (request.getConversationId() == null) {
                return ResponseEntity.ok(ApiResponse.error(
                        ApiResultCode.PARAM_VALID_ERR,
                        "대화 ID는 필수입니다."));
            }

            ConversationId conversationId = ConversationId.of(request.getConversationId());
            List<ConversationMessage> history = manageConversationUseCase.getHistory(conversationId);

            String contextPrompt = buildContextPrompt(history, request.getPrompt());
            Prompt prompt = Prompt.builder()
                    .content(contextPrompt)
                    .template(request.getTemplate())
                    .variables(request.getVariables())
                    .build();

            LLMGenerationRequest domainRequest = LLMMapper.toDomain(request, prompt);
            var result = generateLLMUseCase.generateWithHistory(domainRequest, request.getConversationId());

            manageConversationUseCase.saveMessage(
                    conversationId,
                    LLMMapper.toDomainMessage("user", request.getPrompt()));
            manageConversationUseCase.saveMessage(
                    conversationId,
                    LLMMapper.toDomainMessage("assistant", result.getContent()));

            LLMResponse response = LLMMapper.toDto(result, request.getConversationId());
            return ResponseEntity.ok(ApiResponse.ok(response));

        } catch (Exception e) {
            log.error("히스토리 포함 LLM 요청 처리 오류", e);
            return ResponseEntity.ok(ApiResponse.error(
                    ApiResultCode.LLM_SERVICE_ERROR,
                    "LLM 요청 처리 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    @PostMapping(value = "/generate/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateStream(@Valid @RequestBody LLMRequest request) {
        SseEmitter emitter = new SseEmitter(300000L);

        try {
            Prompt prompt = buildPrompt(request);
            LLMGenerationRequest domainRequest = LLMMapper.toDomain(request, prompt);

            generateLLMUseCase.generateStream(domainRequest, chunk -> {
                try {
                    emitter.send(SseEmitter.event()
                            .data(chunk)
                            .name("chunk"));
                } catch (Exception e) {
                    log.error("스트리밍 전송 오류", e);
                    emitter.completeWithError(e);
                }
            });

            emitter.onCompletion(() -> log.debug("스트리밍 완료"));
            emitter.onError(ex -> log.error("스트리밍 오류", ex));
            emitter.onTimeout(() -> {
                log.warn("스트리밍 타임아웃");
                emitter.complete();
            });

        } catch (Exception e) {
            log.error("스트리밍 요청 처리 오류", e);
            emitter.completeWithError(e);
        }

        return emitter;
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, Object>>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("available", generateLLMUseCase.isAvailable());
        status.put("service", "service.llm");
        status.put("model", "Qwen2.5-7B-Instruct-GGUF");

        return ResponseEntity.ok(ApiResponse.ok(status));
    }

    private Prompt buildPrompt(LLMRequest request) {
        if (request.getTemplate() != null && !request.getTemplate().isEmpty()) {
            if ("healthcare".equals(request.getTemplate()) && request.getVariables() != null) {
                return promptBuilder.buildHealthcarePrompt(request.getVariables());
            } else if (request.getVariables() != null) {
                return promptBuilder.buildGeneralPrompt(request.getPrompt(), request.getVariables());
            }
        }
        return Prompt.builder()
                .content(request.getPrompt())
                .template(request.getTemplate())
                .variables(request.getVariables())
                .systemPrompt(request.getSystemPrompt())
                .build();
    }

    private Map<String, Object> convertToBioInfoMap(HealthcarePromptRequest request) {
        Map<String, Object> map = new HashMap<>();
        map.put("userNm", request.getUserName());
        map.put("age", request.getAge());
        map.put("heartrate", request.getHeartRate());
        map.put("bloodpressMax", request.getBloodPressureMax());
        map.put("bloodpressMin", request.getBloodPressureMin());
        map.put("temperature", request.getTemperature());
        map.put("stress", request.getStress());
        map.put("oxygenSaturation", request.getOxygenSaturation());
        map.put("steps", request.getSteps());
        map.put("totalCholesterol", request.getTotalCholesterol());
        map.put("fastingBloodSugar", request.getFastingBloodSugar());
        map.put("hba1c", request.getHba1c());
        map.put("bmi", request.getBmi());
        map.put("userQuestion", request.getUserQuestion());
        map.put("includeDiseaseRecommendation", request.getIncludeDiseaseRecommendation());
        map.put("includeFoodRecommendation", request.getIncludeFoodRecommendation());
        map.put("includeExerciseRecommendation", request.getIncludeExerciseRecommendation());
        map.put("recommendationCount", request.getRecommendationCount());
        return map;
    }

    private String buildContextPrompt(List<ConversationMessage> history, String currentPrompt) {
        if (history == null || history.isEmpty()) {
            return currentPrompt;
        }

        StringBuilder contextPrompt = new StringBuilder();
        for (ConversationMessage msg : history) {
            contextPrompt.append(msg.getRole()).append(": ").append(msg.getContent()).append("\n\n");
        }
        contextPrompt.append("user: ").append(currentPrompt);
        return contextPrompt.toString();
    }
}
