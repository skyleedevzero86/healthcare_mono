package com.sleekydz86.service.llm.application.decision;

import com.sleekydz86.service.llm.domain.agent.AgentState;
import com.sleekydz86.service.llm.domain.decision.DecisionEngine;
import com.sleekydz86.service.llm.domain.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RuleBasedDecisionEngine implements DecisionEngine {
    
    @Override
    public DecisionResult decide(AgentState state, String goal, Map<String, Object> context, List<Tool> availableTools) {
        if (state == null || goal == null || goal.isEmpty()) {
            return DecisionResult.fail("상태 또는 목표가 유효하지 않습니다.");
        }
        
        if (state.getCurrentPlan() == null || state.getCurrentPlan().isCompleted()) {
            return createInitialPlan(goal, context, availableTools);
        }
        
        AgentPlan.PlanStep currentStep = state.getCurrentPlan().getCurrentStep();
        if (currentStep == null) {
            return DecisionResult.complete(context.get("result"));
        }
        
        if (currentStep.getStatus() == AgentPlan.StepStatus.COMPLETED) {
            if (state.getCurrentPlan().hasNextStep()) {
                state.getCurrentPlan().moveToNextStep();
                return decideNextStep(state, availableTools);
            } else {
                return DecisionResult.complete(state.getContext("finalResult"));
            }
        }
        
        if (currentStep.getStatus() == AgentPlan.StepStatus.FAILED) {
            return handleFailedStep(state, currentStep, availableTools);
        }
        
        return executeCurrentStep(state, currentStep, availableTools);
    }
    
    private DecisionResult createInitialPlan(String goal, Map<String, Object> context, List<Tool> availableTools) {
        String lowerGoal = goal.toLowerCase();
        
        if (lowerGoal.contains("질병") || lowerGoal.contains("예측") || lowerGoal.contains("disease")) {
            Tool tool = findTool(availableTools, "disease_prediction");
            if (tool != null) {
                return DecisionResult.useTool(tool.getName(), context, 
                    "질병 예측 목표를 위해 질병 예측 도구를 사용합니다.", 0.9);
            }
        }
        
        if (lowerGoal.contains("건강") || lowerGoal.contains("조언") || lowerGoal.contains("health")) {
            Tool tool = findTool(availableTools, "health_analysis");
            if (tool != null) {
                return DecisionResult.useTool(tool.getName(), context,
                    "건강 분석 목표를 위해 건강 분석 도구를 사용합니다.", 0.9);
            }
        }
        
        if (lowerGoal.contains("데이터") || lowerGoal.contains("조회") || lowerGoal.contains("get")) {
            Tool tool = findTool(availableTools, "get_health_data");
            if (tool != null) {
                return DecisionResult.useTool(tool.getName(), context,
                    "건강 데이터 조회 목표를 위해 데이터 조회 도구를 사용합니다.", 0.9);
            }
        }
        
        return DecisionResult.think("목표를 분석하여 적절한 도구를 선택해야 합니다.", 0.7);
    }
    
    private DecisionResult decideNextStep(AgentState state, List<Tool> availableTools) {
        AgentPlan.PlanStep nextStep = state.getCurrentPlan().getCurrentStep();
        if (nextStep == null) {
            return DecisionResult.complete(state.getContext("finalResult"));
        }
        
        return DecisionResult.useTool(nextStep.getToolName(), nextStep.getToolInput(),
            "계획된 다음 단계를 실행합니다: " + nextStep.getAction(), 0.8);
    }
    
    private DecisionResult executeCurrentStep(AgentState state, AgentPlan.PlanStep step, List<Tool> availableTools) {
        if (step.getToolName() == null || step.getToolName().isEmpty()) {
            return DecisionResult.think("현재 단계에 도구가 지정되지 않았습니다. 다음 행동을 결정해야 합니다.", 0.6);
        }
        
        Tool tool = findTool(availableTools, step.getToolName());
        if (tool == null) {
            return DecisionResult.fail("도구를 찾을 수 없습니다: " + step.getToolName());
        }
        
        return DecisionResult.useTool(tool.getName(), step.getToolInput(),
            "계획된 단계를 실행합니다: " + step.getAction(), 0.9);
    }
    
    private DecisionResult handleFailedStep(AgentState state, AgentPlan.PlanStep failedStep, List<Tool> availableTools) {
        int retryCount = (Integer) state.getContext().getOrDefault("retryCount_" + failedStep.getStepNumber(), 0);
        
        if (retryCount < 2) {
            state.getContext().put("retryCount_" + failedStep.getStepNumber(), retryCount + 1);
            return DecisionResult.useTool(failedStep.getToolName(), failedStep.getToolInput(),
                "실패한 단계를 재시도합니다. (시도: " + (retryCount + 1) + ")", 0.7);
        }
        
        return DecisionResult.fail("단계 실행이 반복적으로 실패했습니다: " + failedStep.getError());
    }
    
    private Tool findTool(List<Tool> tools, String namePattern) {
        if (tools == null) {
            return null;
        }
        
        return tools.stream()
                .filter(tool -> tool.getName().toLowerCase().contains(namePattern.toLowerCase()))
                .findFirst()
                .orElse(null);
    }
}

