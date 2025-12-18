package com.sleekydz86.service.llm.controller;

import com.sleekydz86.service.llm.dto.*;
import com.sleekydz86.service.llm.service.LLMService;
import com.sleekydz86.service.llm.service.PromptTemplateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/llm")
@RequiredArgsConstructor
public class LLMController {

    private final LLMService llmService;
    private final PromptTemplateService promptTemplateService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<LLMResponse>> generate(@Valid @RequestBody LLMRequest request) {
        try {
            log.info("LLM 요청 수신: userId={}, template={}", request.getUserId(), request.getTemplate());
            
            if (!llmService.isAvailable()) {
                return ResponseEntity.ok(ApiResponse.error(
                        ApiResultCode.LLM_SERVICE_ERROR,
                        "LLM 서비스가 현재 사용할 수 없습니다."
                ));
            }
            
            LLMResponse response = llmService.generate(request);
            return ResponseEntity.ok(ApiResponse.ok(response));
            
        } catch (Exception e) {
            log.error("LLM 요청 처리 오류", e);
            return ResponseEntity.ok(ApiResponse.error(
                    ApiResultCode.LLM_SERVICE_ERROR,
                    "LLM 요청 처리 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/healthcare")
    public ResponseEntity<ApiResponse<LLMResponse>> generateHealthcare(
            @Valid @RequestBody HealthcarePromptRequest request) {
        try {
            log.info("헬스케어 LLM 요청 수신: userId={}", request.getUserId());
            
            if (!llmService.isAvailable()) {
                return ResponseEntity.ok(ApiResponse.error(
                        ApiResultCode.LLM_SERVICE_ERROR,
                        "LLM 서비스가 현재 사용할 수 없습니다."
                ));
            }
            
            String prompt = promptTemplateService.buildHealthcarePrompt(request);
            
            LLMRequest llmRequest = LLMRequest.builder()
                    .prompt(prompt)
                    .template("healthcare")
                    .userId(request.getUserId())
                    .build();
            
            LLMResponse response = llmService.generate(llmRequest);
            return ResponseEntity.ok(ApiResponse.ok(response));
            
        } catch (Exception e) {
            log.error("헬스케어 LLM 요청 처리 오류", e);
            return ResponseEntity.ok(ApiResponse.error(
                    ApiResultCode.LLM_SERVICE_ERROR,
                    "헬스케어 LLM 요청 처리 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/chat_ai")
    public ResponseEntity<ApiResponse<Map<String, Object>>> chatAi(@RequestBody Map<String, Object> request) {
        try {
            log.info("기존 형식 LLM 요청 수신");
            
            if (!llmService.isAvailable()) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("aiResponse", "LLM 서비스가 현재 사용할 수 없습니다.");
                return ResponseEntity.ok(ApiResponse.ok(errorResult));
            }
            
            BioInfoDto bioInfo = BioInfoDto.fromMap(request);
            String prompt = promptTemplateService.buildHealthcarePromptFromBioInfo(bioInfo);
            
            LLMRequest llmRequest = LLMRequest.builder()
                    .prompt(prompt)
                    .template("healthcare")
                    .userId(request.get("userId") != null ? request.get("userId").toString() : null)
                    .build();
            
            LLMResponse response = llmService.generate(llmRequest);
            
            Map<String, Object> result = new HashMap<>();
            result.put("aiResponse", response.getResponse());
            
            return ResponseEntity.ok(ApiResponse.ok(result));
            
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
            
            if (!llmService.isAvailable()) {
                return ResponseEntity.ok(ApiResponse.error(
                        ApiResultCode.LLM_SERVICE_ERROR,
                        "LLM 서비스가 현재 사용할 수 없습니다."
                ));
            }
            
            LLMResponse response = llmService.generateWithHistory(request);
            return ResponseEntity.ok(ApiResponse.ok(response));
            
        } catch (Exception e) {
            log.error("히스토리 포함 LLM 요청 처리 오류", e);
            return ResponseEntity.ok(ApiResponse.error(
                    ApiResultCode.LLM_SERVICE_ERROR,
                    "LLM 요청 처리 중 오류가 발생했습니다: " + e.getMessage()
            ));
        }
    }

    @PostMapping(value = "/generate/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter generateStream(@Valid @RequestBody LLMRequest request) {
        SseEmitter emitter = new SseEmitter(300000L);
        
        try {
            llmService.generateStream(request, chunk -> {
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
        status.put("available", llmService.isAvailable());
        status.put("service", "service.llm");
        status.put("model", "Qwen2.5-7B-Instruct-GGUF");
        
        return ResponseEntity.ok(ApiResponse.ok(status));
    }
}

