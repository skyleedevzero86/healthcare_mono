package com.sleekydz86.service.llm.application.agent;

import com.sleekydz86.service.llm.domain.decision.DecisionEngine;
import com.sleekydz86.service.llm.domain.tool.ToolRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HealthcareAgentFactory {
    
    @Qualifier("ruleBasedDecisionEngine")
    private final DecisionEngine ruleBasedDecisionEngine;
    
    @Qualifier("llmBasedDecisionEngine")
    private final DecisionEngine llmBasedDecisionEngine;
    
    private final ToolRegistry toolRegistry;
    
    public HealthcareAgent create() {
        return create("rule");
    }
    
    public HealthcareAgent create(String decisionEngineType) {
        DecisionEngine decisionEngine = "llm".equalsIgnoreCase(decisionEngineType) ? 
            llmBasedDecisionEngine : ruleBasedDecisionEngine;
        
        return new HealthcareAgent(decisionEngine, toolRegistry);
    }
}

