package com.sleekydz86.service.llm.domain.decision;

import com.sleekydz86.service.llm.domain.agent.AgentState;
import com.sleekydz86.service.llm.domain.agent.AgentPlan;
import com.sleekydz86.service.llm.domain.tool.Tool;

import java.util.List;
import java.util.Map;

public interface DecisionEngine {
    DecisionResult decide(AgentState state, String goal, Map<String, Object> context, List<Tool> availableTools);
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    class DecisionResult {
        private DecisionType type;
        private String action;
        private String toolName;
        private Object toolInput;
        private String reasoning;
        private double confidence;
        private Map<String, Object> metadata;
        
        public static DecisionResult useTool(String toolName, Object toolInput, String reasoning, double confidence) {
            return DecisionResult.builder()
                    .type(DecisionType.USE_TOOL)
                    .toolName(toolName)
                    .toolInput(toolInput)
                    .reasoning(reasoning)
                    .confidence(confidence)
                    .build();
        }
        
        public static DecisionResult think(String reasoning, double confidence) {
            return DecisionResult.builder()
                    .type(DecisionType.THINK)
                    .reasoning(reasoning)
                    .confidence(confidence)
                    .build();
        }
        
        public static DecisionResult complete(Object result) {
            return DecisionResult.builder()
                    .type(DecisionType.COMPLETE)
                    .metadata(Map.of("result", result))
                    .confidence(1.0)
                    .build();
        }
        
        public static DecisionResult fail(String error) {
            return DecisionResult.builder()
                    .type(DecisionType.FAIL)
                    .reasoning(error)
                    .confidence(0.0)
                    .build();
        }
    }
    
    enum DecisionType {
        USE_TOOL,
        THINK,
        COMPLETE,
        FAIL,
        WAIT
    }
}

