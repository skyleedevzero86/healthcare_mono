package com.sleekydz86.service.llm.domain.agent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentPlan {
    private String planId;
    private String goal;
    private List<PlanStep> steps;
    private int currentStepIndex;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public static AgentPlan create(String goal) {
        return AgentPlan.builder()
                .planId(java.util.UUID.randomUUID().toString())
                .goal(goal)
                .steps(new ArrayList<>())
                .currentStepIndex(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    public void addStep(PlanStep step) {
        if (steps == null) {
            steps = new ArrayList<>();
        }
        steps.add(step);
        updatedAt = LocalDateTime.now();
    }
    
    public PlanStep getCurrentStep() {
        if (steps == null || steps.isEmpty() || currentStepIndex >= steps.size()) {
            return null;
        }
        return steps.get(currentStepIndex);
    }
    
    public boolean hasNextStep() {
        return steps != null && currentStepIndex < steps.size() - 1;
    }
    
    public void moveToNextStep() {
        if (hasNextStep()) {
            currentStepIndex++;
            updatedAt = LocalDateTime.now();
        }
    }
    
    public boolean isCompleted() {
        return steps != null && currentStepIndex >= steps.size();
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlanStep {
        private int stepNumber;
        private String action;
        private String toolName;
        private Object toolInput;
        private String expectedOutput;
        private StepStatus status;
        private Object result;
        private String error;
        
        public static PlanStep create(int stepNumber, String action, String toolName, Object toolInput) {
            return PlanStep.builder()
                    .stepNumber(stepNumber)
                    .action(action)
                    .toolName(toolName)
                    .toolInput(toolInput)
                    .status(StepStatus.PENDING)
                    .build();
        }
        
        public void markExecuting() {
            this.status = StepStatus.EXECUTING;
        }
        
        public void markCompleted(Object result) {
            this.status = StepStatus.COMPLETED;
            this.result = result;
        }
        
        public void markFailed(String error) {
            this.status = StepStatus.FAILED;
            this.error = error;
        }
    }
    
    public enum StepStatus {
        PENDING,
        EXECUTING,
        COMPLETED,
        FAILED,
        SKIPPED
    }
}

