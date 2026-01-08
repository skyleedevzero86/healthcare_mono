package com.sleekydz86.service.llm.config;

import com.sleekydz86.service.llm.application.decision.LLMBasedDecisionEngine;
import com.sleekydz86.service.llm.application.decision.RuleBasedDecisionEngine;
import com.sleekydz86.service.llm.domain.decision.DecisionEngine;
import com.sleekydz86.service.llm.domain.tool.Tool;
import com.sleekydz86.service.llm.domain.tool.ToolRegistry;
import com.sleekydz86.service.llm.infrastructure.adapter.tool.DiseasePredictionTool;
import com.sleekydz86.service.llm.infrastructure.adapter.tool.GetHealthDataTool;
import com.sleekydz86.service.llm.infrastructure.adapter.tool.HealthAnalysisTool;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AgentConfig {
    
    @Bean
    public ToolRegistry toolRegistry(
            DiseasePredictionTool diseasePredictionTool,
            HealthAnalysisTool healthAnalysisTool,
            GetHealthDataTool getHealthDataTool) {
        List<Tool> tools = List.of(
                diseasePredictionTool,
                healthAnalysisTool,
                getHealthDataTool
        );
        return new ToolRegistry(tools);
    }
    
    @Bean("ruleBasedDecisionEngine")
    public DecisionEngine ruleBasedDecisionEngine() {
        return new RuleBasedDecisionEngine();
    }
    
    @Bean("llmBasedDecisionEngine")
    public DecisionEngine llmBasedDecisionEngine(
            com.sleekydz86.service.llm.ports.inbound.GenerateLLMUseCase generateLLMUseCase,
            com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
        return new LLMBasedDecisionEngine(generateLLMUseCase, objectMapper);
    }
}

