package com.sleekydz86.service.llm.domain.agent;

import com.sleekydz86.service.llm.domain.model.LLMGenerationRequest;
import com.sleekydz86.service.llm.domain.model.LLMGenerationResult;
import com.sleekydz86.service.llm.domain.tool.Tool;

import java.util.List;
import java.util.Map;

public interface Agent {
    String getAgentId();
    String getAgentType();
    AgentState getState();
    AgentMemory getMemory();
    List<Tool> getAvailableTools();
    
    AgentExecutionResult execute(String goal, Map<String, Object> initialContext);
    AgentExecutionResult continueExecution();
    void pause();
    void resume();
    void reset();
    
    boolean isAvailable();
    
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    class AgentExecutionResult {
        private boolean completed;
        private AgentStatus status;
        private Object result;
        private String error;
        private AgentPlan plan;
        private List<AgentPlan.PlanStep> executedSteps;
        private long totalExecutionTimeMs;
        
        public static AgentExecutionResult success(Object result, AgentPlan plan, List<AgentPlan.PlanStep> executedSteps, long executionTimeMs) {
            return AgentExecutionResult.builder()
                    .completed(true)
                    .status(AgentStatus.COMPLETED)
                    .result(result)
                    .plan(plan)
                    .executedSteps(executedSteps)
                    .totalExecutionTimeMs(executionTimeMs)
                    .build();
        }
        
        public static AgentExecutionResult inProgress(AgentStatus status, AgentPlan plan, List<AgentPlan.PlanStep> executedSteps, long executionTimeMs) {
            return AgentExecutionResult.builder()
                    .completed(false)
                    .status(status)
                    .plan(plan)
                    .executedSteps(executedSteps)
                    .totalExecutionTimeMs(executionTimeMs)
                    .build();
        }
        
        public static AgentExecutionResult failure(String error, AgentPlan plan, List<AgentPlan.PlanStep> executedSteps, long executionTimeMs) {
            return AgentExecutionResult.builder()
                    .completed(true)
                    .status(AgentStatus.FAILED)
                    .error(error)
                    .plan(plan)
                    .executedSteps(executedSteps)
                    .totalExecutionTimeMs(executionTimeMs)
                    .build();
        }
    }
}

