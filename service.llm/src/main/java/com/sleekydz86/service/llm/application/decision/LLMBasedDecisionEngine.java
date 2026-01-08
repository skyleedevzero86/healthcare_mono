package com.sleekydz86.service.llm.application.decision;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleekydz86.service.llm.domain.agent.AgentState;
import com.sleekydz86.service.llm.domain.decision.DecisionEngine;
import com.sleekydz86.service.llm.domain.model.LLMGenerationRequest;
import com.sleekydz86.service.llm.domain.model.LLMGenerationResult;
import com.sleekydz86.service.llm.domain.model.Prompt;
import com.sleekydz86.service.llm.domain.tool.Tool;
import com.sleekydz86.service.llm.ports.inbound.GenerateLLMUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class LLMBasedDecisionEngine implements DecisionEngine {
    
    private final GenerateLLMUseCase generateLLMUseCase;
    private final ObjectMapper objectMapper;
    
    @Override
    public DecisionResult decide(AgentState state, String goal, Map<String, Object> context, List<Tool> availableTools) {
        if (!generateLLMUseCase.isAvailable()) {
            return DecisionResult.fail("LLM 서비스가 사용할 수 없습니다.");
        }
        
        String decisionPrompt = buildDecisionPrompt(state, goal, context, availableTools);
        
        Prompt prompt = Prompt.builder()
                .content(decisionPrompt)
                .systemPrompt(buildSystemPrompt())
                .build();
        
        LLMGenerationRequest request = LLMGenerationRequest.builder()
                .prompt(prompt)
                .maxTokens(1024)
                .temperature(0.3)
                .topP(0.9)
                .topK(40)
                .build();
        
        try {
            LLMGenerationResult result = generateLLMUseCase.generate(request);
            return parseDecision(result.getContent(), availableTools);
        } catch (Exception e) {
            log.error("LLM 기반 의사결정 중 오류 발생", e);
            return DecisionResult.fail("의사결정 처리 중 오류: " + e.getMessage());
        }
    }
    
    private String buildSystemPrompt() {
        return """
            당신은 AI Agent의 의사결정 엔진입니다. 주어진 목표와 현재 상태를 분석하여 다음 행동을 결정해야 합니다.
            
            응답 형식 (JSON):
            {
              "decision": "USE_TOOL|THINK|COMPLETE|FAIL",
              "toolName": "도구 이름 (USE_TOOL인 경우)",
              "toolInput": {도구 입력 데이터},
              "reasoning": "의사결정 이유",
              "confidence": 0.0-1.0
            }
            """;
    }
    
    private String buildDecisionPrompt(AgentState state, String goal, Map<String, Object> context, List<Tool> availableTools) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("목표: ").append(goal).append("\n\n");
        
        prompt.append("현재 상태:\n");
        prompt.append("- 상태: ").append(state.getStatus()).append("\n");
        prompt.append("- 단계: ").append(state.getStepCount()).append("/").append(state.getMaxSteps()).append("\n");
        
        if (state.getCurrentPlan() != null) {
            AgentPlan.PlanStep currentStep = state.getCurrentPlan().getCurrentStep();
            if (currentStep != null) {
                prompt.append("- 현재 단계: ").append(currentStep.getAction()).append("\n");
                prompt.append("- 단계 상태: ").append(currentStep.getStatus()).append("\n");
            }
        }
        
        if (context != null && !context.isEmpty()) {
            prompt.append("\n컨텍스트:\n");
            context.forEach((key, value) -> 
                prompt.append("- ").append(key).append(": ").append(value).append("\n"));
        }
        
        if (state.getMemory() != null) {
            String recentContext = state.getMemory().getRecentContext(5);
            if (!recentContext.isEmpty()) {
                prompt.append("\n최근 메모리:\n").append(recentContext);
            }
        }
        
        prompt.append("\n사용 가능한 도구:\n");
        for (Tool tool : availableTools) {
            prompt.append("- ").append(tool.getName()).append(": ").append(tool.getDescription()).append("\n");
        }
        
        prompt.append("\n다음 행동을 결정하세요:");
        
        return prompt.toString();
    }
    
    private DecisionResult parseDecision(String llmResponse, List<Tool> availableTools) {
        try {
            String cleaned = cleanJsonResponse(llmResponse);
            JsonNode root = objectMapper.readTree(cleaned);
            
            String decisionType = root.path("decision").asText("THINK");
            String toolName = root.path("toolName").asText();
            JsonNode toolInputNode = root.path("toolInput");
            String reasoning = root.path("reasoning").asText("의사결정이 생성되었습니다.");
            double confidence = root.path("confidence").asDouble(0.7);
            
            Map<String, Object> toolInput = new HashMap<>();
            if (toolInputNode.isObject()) {
                toolInputNode.fields().forEachRemaining(entry -> 
                    toolInput.put(entry.getKey(), parseJsonValue(entry.getValue())));
            }
            
            switch (decisionType) {
                case "USE_TOOL":
                    if (toolName != null && !toolName.isEmpty()) {
                        return DecisionResult.useTool(toolName, toolInput, reasoning, confidence);
                    }
                    return DecisionResult.think(reasoning, confidence);
                    
                case "COMPLETE":
                    Object result = root.path("result").asText();
                    return DecisionResult.complete(result);
                    
                case "FAIL":
                    return DecisionResult.fail(reasoning);
                    
                default:
                    return DecisionResult.think(reasoning, confidence);
            }
            
        } catch (Exception e) {
            log.error("의사결정 응답 파싱 오류", e);
            return DecisionResult.think("응답 파싱 중 오류가 발생했습니다. 다시 생각해봅니다.", 0.5);
        }
    }
    
    private String cleanJsonResponse(String response) {
        String cleaned = response.trim();
        int jsonStart = cleaned.indexOf("{");
        int jsonEnd = cleaned.lastIndexOf("}");
        
        if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
            cleaned = cleaned.substring(jsonStart, jsonEnd + 1);
        }
        
        cleaned = cleaned.replaceAll("```json", "");
        cleaned = cleaned.replaceAll("```", "");
        return cleaned.trim();
    }
    
    private Object parseJsonValue(JsonNode node) {
        if (node.isTextual()) {
            return node.asText();
        } else if (node.isNumber()) {
            if (node.isInt()) {
                return node.asInt();
            } else if (node.isLong()) {
                return node.asLong();
            } else {
                return node.asDouble();
            }
        } else if (node.isBoolean()) {
            return node.asBoolean();
        } else if (node.isArray()) {
            return node.toString();
        } else if (node.isObject()) {
            return node.toString();
        }
        return node.asText();
    }
}

