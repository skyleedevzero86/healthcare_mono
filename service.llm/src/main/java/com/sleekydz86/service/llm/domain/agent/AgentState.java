package com.sleekydz86.service.llm.domain.agent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentState {
    private String agentId;
    private AgentStatus status;
    private String currentGoal;
    private AgentPlan currentPlan;
    private Map<String, Object> context;
    private LocalDateTime lastUpdated;
    private int stepCount;
    private int maxSteps;
    
    public static AgentState create(String agentId, String goal, int maxSteps) {
        return AgentState.builder()
                .agentId(agentId)
                .status(AgentStatus.IDLE)
                .currentGoal(goal)
                .context(new HashMap<>())
                .lastUpdated(LocalDateTime.now())
                .stepCount(0)
                .maxSteps(maxSteps)
                .build();
    }
    
    public void updateStatus(AgentStatus newStatus) {
        this.status = newStatus;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public void incrementStep() {
        this.stepCount++;
        this.lastUpdated = LocalDateTime.now();
    }
    
    public boolean canContinue() {
        return stepCount < maxSteps && status != AgentStatus.COMPLETED && status != AgentStatus.FAILED;
    }
    
    public void addContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
    }
    
    public Object getContext(String key) {
        return context != null ? context.get(key) : null;
    }
}

