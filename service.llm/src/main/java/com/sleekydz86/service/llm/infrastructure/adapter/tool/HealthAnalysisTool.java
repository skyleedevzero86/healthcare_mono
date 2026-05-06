package com.sleekydz86.service.llm.infrastructure.adapter.tool;

import com.sleekydz86.service.llm.domain.model.LLMGenerationRequest;
import com.sleekydz86.service.llm.domain.model.LLMGenerationResult;
import com.sleekydz86.service.llm.domain.model.Prompt;
import com.sleekydz86.service.llm.domain.service.HealthcarePromptBuilder;
import com.sleekydz86.service.llm.domain.tool.Tool;
import com.sleekydz86.service.llm.ports.inbound.GenerateLLMUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HealthAnalysisTool implements Tool {
    
    private final GenerateLLMUseCase generateLLMUseCase;
    private final HealthcarePromptBuilder promptBuilder;
    
    @Override
    public String getName() {
        return "health_analysis";
    }
    
    @Override
    public String getDescription() {
        return "건강 데이터를 종합적으로 분석하여 건강 상태를 평가하고 개인화된 조언을 제공합니다.";
    }
    
    @Override
    public ToolSchema getSchema() {
        Map<String, ParameterSchema> parameters = new HashMap<>();
        parameters.put("userId", ParameterSchema.builder()
                .type("string")
                .description("사용자 ID")
                .required(true)
                .build());
        parameters.put("userName", ParameterSchema.builder()
                .type("string")
                .description("사용자 이름")
                .required(false)
                .build());
        parameters.put("age", ParameterSchema.builder()
                .type("integer")
                .description("나이")
                .required(false)
                .build());
        parameters.put("heartRate", ParameterSchema.builder()
                .type("integer")
                .description("심박수")
                .required(false)
                .build());
        parameters.put("bloodPressureMax", ParameterSchema.builder()
                .type("integer")
                .description("수축기 혈압")
                .required(false)
                .build());
        parameters.put("bloodPressureMin", ParameterSchema.builder()
                .type("integer")
                .description("이완기 혈압")
                .required(false)
                .build());
        parameters.put("temperature", ParameterSchema.builder()
                .type("number")
                .description("체온")
                .required(false)
                .build());
        parameters.put("stress", ParameterSchema.builder()
                .type("integer")
                .description("스트레스 지수")
                .required(false)
                .build());
        parameters.put("includeDiseaseRecommendation", ParameterSchema.builder()
                .type("boolean")
                .description("질병 권장사항 포함 여부")
                .required(false)
                .defaultValue(true)
                .build());
        parameters.put("includeFoodRecommendation", ParameterSchema.builder()
                .type("boolean")
                .description("식이 권장사항 포함 여부")
                .required(false)
                .defaultValue(true)
                .build());
        parameters.put("recommendationCount", ParameterSchema.builder()
                .type("integer")
                .description("권장사항 개수")
                .required(false)
                .defaultValue(3)
                .build());
        
        return ToolSchema.builder()
                .name(getName())
                .description(getDescription())
                .parameters(parameters)
                .build();
    }
    
    @Override
    public ToolResult execute(Map<String, Object> input) {
        long startTime = System.currentTimeMillis();
        
        try {
            if (input == null || input.isEmpty()) {
                return ToolResult.failure("입력 데이터가 필요합니다.", System.currentTimeMillis() - startTime);
            }
            
            String userId = (String) input.get("userId");
            if (userId == null || userId.isEmpty()) {
                return ToolResult.failure("사용자 ID가 필요합니다.", System.currentTimeMillis() - startTime);
            }
            
            Prompt prompt = promptBuilder.buildHealthcarePrompt(input);
            
            LLMGenerationRequest request = LLMGenerationRequest.builder()
                    .prompt(prompt)
                    .maxTokens(2048)
                    .temperature(0.7)
                    .topP(0.9)
                    .topK(40)
                    .repeatPenalty(1.1)
                    .requestType("healthcare")
                    .build();
            
            LLMGenerationResult result = generateLLMUseCase.generate(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("analysis", result.getContent());
            response.put("tokensUsed", result.getTokensUsed());
            response.put("processingTimeMs", result.getProcessingTimeMs());
            
            return ToolResult.success(response, System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            log.error("건강 분석 도구 실행 오류", e);
            return ToolResult.failure("건강 분석 중 오류가 발생했습니다: " + e.getMessage(),
                    System.currentTimeMillis() - startTime);
        }
    }
    
    @Override
    public boolean canHandle(String action) {
        return action != null && (
            action.toLowerCase().contains("health") ||
            action.toLowerCase().contains("건강") ||
            action.toLowerCase().contains("analysis") ||
            action.toLowerCase().contains("분석") ||
            action.toLowerCase().contains("advice") ||
            action.toLowerCase().contains("조언")
        );
    }
}

