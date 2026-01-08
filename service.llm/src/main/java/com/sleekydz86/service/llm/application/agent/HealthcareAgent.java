package com.sleekydz86.service.llm.application.agent;

import com.sleekydz86.service.llm.domain.agent.*;
import com.sleekydz86.service.llm.domain.decision.DecisionEngine;
import com.sleekydz86.service.llm.domain.tool.Tool;
import com.sleekydz86.service.llm.domain.tool.ToolRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class HealthcareAgent implements Agent {
    
    private final DecisionEngine decisionEngine;
    private final ToolRegistry toolRegistry;
    private final String agentId;
    private AgentState state;
    private AgentMemory memory;
    
    public HealthcareAgent(DecisionEngine decisionEngine, ToolRegistry toolRegistry) {
        this.decisionEngine = decisionEngine;
        this.toolRegistry = toolRegistry;
        this.agentId = "healthcare-agent-" + UUID.randomUUID().toString().substring(0, 8);
        this.memory = AgentMemory.create(agentId, 20);
        reset();
    }
    
    @Override
    public String getAgentId() {
        return agentId;
    }
    
    @Override
    public String getAgentType() {
        return "healthcare";
    }
    
    @Override
    public AgentState getState() {
        return state;
    }
    
    @Override
    public AgentMemory getMemory() {
        return memory;
    }
    
    @Override
    public List<Tool> getAvailableTools() {
        return toolRegistry.getAllTools();
    }
    
    @Override
    public AgentExecutionResult execute(String goal, Map<String, Object> initialContext) {
        long startTime = System.currentTimeMillis();
        
        try {
            reset();
            state.setCurrentGoal(goal);
            if (initialContext != null) {
                state.getContext().putAll(initialContext);
            }
            
            state.updateStatus(AgentStatus.PLANNING);
            memory.addShortTerm("목표 설정: " + goal, AgentMemory.MemoryType.DECISION);
            
            List<AgentPlan.PlanStep> executedSteps = new ArrayList<>();
            
            while (state.canContinue()) {
                state.incrementStep();
                
                DecisionEngine.DecisionResult decision = decisionEngine.decide(
                    state, goal, state.getContext(), getAvailableTools());
                
                memory.addShortTerm("의사결정: " + decision.getReasoning(), 
                    AgentMemory.MemoryType.DECISION);
                
                switch (decision.getType()) {
                    case USE_TOOL:
                        AgentExecutionResult toolResult = executeTool(decision, executedSteps);
                        if (toolResult.isCompleted()) {
                            return toolResult;
                        }
                        break;
                        
                    case THINK:
                        state.updateStatus(AgentStatus.THINKING);
                        memory.addShortTerm("생각 중: " + decision.getReasoning(), 
                            AgentMemory.MemoryType.DECISION);
                        state.updateStatus(AgentStatus.EXECUTING);
                        break;
                        
                    case COMPLETE:
                        state.updateStatus(AgentStatus.COMPLETED);
                        memory.addShortTerm("작업 완료", AgentMemory.MemoryType.DECISION);
                        return AgentExecutionResult.success(
                            decision.getMetadata() != null ? decision.getMetadata().get("result") : null,
                            state.getCurrentPlan(),
                            executedSteps,
                            System.currentTimeMillis() - startTime);
                        
                    case FAIL:
                        state.updateStatus(AgentStatus.FAILED);
                        memory.addShortTerm("실패: " + decision.getReasoning(), 
                            AgentMemory.MemoryType.ERROR);
                        return AgentExecutionResult.failure(
                            decision.getReasoning(),
                            state.getCurrentPlan(),
                            executedSteps,
                            System.currentTimeMillis() - startTime);
                }
            }
            
            if (state.getStatus() == AgentStatus.COMPLETED) {
                return AgentExecutionResult.success(
                    state.getContext("finalResult"),
                    state.getCurrentPlan(),
                    executedSteps,
                    System.currentTimeMillis() - startTime);
            }
            
            return AgentExecutionResult.failure(
                "최대 단계 수에 도달했습니다.",
                state.getCurrentPlan(),
                executedSteps,
                System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            log.error("Agent 실행 중 오류 발생", e);
            state.updateStatus(AgentStatus.FAILED);
            return AgentExecutionResult.failure(
                "Agent 실행 중 오류: " + e.getMessage(),
                state.getCurrentPlan(),
                new ArrayList<>(),
                System.currentTimeMillis());
        }
    }
    
    @Override
    public AgentExecutionResult continueExecution() {
        if (state.getStatus() == AgentStatus.PAUSED) {
            state.updateStatus(AgentStatus.EXECUTING);
            return execute(state.getCurrentGoal(), state.getContext());
        }
        return AgentExecutionResult.failure("Agent가 일시정지 상태가 아닙니다.", 
            state.getCurrentPlan(), new ArrayList<>(), 0);
    }
    
    @Override
    public void pause() {
        if (state.getStatus() == AgentStatus.EXECUTING || 
            state.getStatus() == AgentStatus.THINKING ||
            state.getStatus() == AgentStatus.WAITING_FOR_TOOL) {
            state.updateStatus(AgentStatus.PAUSED);
            memory.addShortTerm("일시정지됨", AgentMemory.MemoryType.DECISION);
        }
    }
    
    @Override
    public void resume() {
        if (state.getStatus() == AgentStatus.PAUSED) {
            state.updateStatus(AgentStatus.EXECUTING);
            memory.addShortTerm("재개됨", AgentMemory.MemoryType.DECISION);
        }
    }
    
    @Override
    public void reset() {
        this.state = AgentState.create(agentId, "", 20);
        this.memory = AgentMemory.create(agentId, 20);
    }
    
    @Override
    public boolean isAvailable() {
        return state.getStatus() != AgentStatus.FAILED && 
               toolRegistry.getAllTools().stream().anyMatch(tool -> true);
    }
    
    private AgentExecutionResult executeTool(DecisionEngine.DecisionResult decision, 
                                           List<AgentPlan.PlanStep> executedSteps) {
        long stepStartTime = System.currentTimeMillis();
        
        try {
            String toolName = decision.getToolName();
            Object toolInput = decision.getToolInput();
            
            if (toolName == null || toolName.isEmpty()) {
                return AgentExecutionResult.failure("도구 이름이 지정되지 않았습니다.",
                    state.getCurrentPlan(), executedSteps, System.currentTimeMillis() - stepStartTime);
            }
            
            Tool tool = toolRegistry.getTool(toolName);
            if (tool == null) {
                return AgentExecutionResult.failure("도구를 찾을 수 없습니다: " + toolName,
                    state.getCurrentPlan(), executedSteps, System.currentTimeMillis() - stepStartTime);
            }
            
            state.updateStatus(AgentStatus.WAITING_FOR_TOOL);
            memory.addShortTerm("도구 실행: " + toolName, AgentMemory.MemoryType.ACTION);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> inputMap = toolInput instanceof Map ? 
                (Map<String, Object>) toolInput : 
                Map.of("input", toolInput);
            
            Tool.ToolResult toolResult = tool.execute(inputMap);
            
            AgentPlan.PlanStep step = AgentPlan.PlanStep.create(
                executedSteps.size() + 1,
                "Execute " + toolName,
                toolName,
                toolInput);
            step.markExecuting();
            
            if (toolResult.isSuccess()) {
                step.markCompleted(toolResult.getData());
                memory.addShortTerm("도구 실행 성공: " + toolName, AgentMemory.MemoryType.RESULT);
                
                state.getContext().put("lastToolResult", toolResult.getData());
                state.getContext().put("lastToolName", toolName);
                
                if (shouldComplete(toolResult.getData())) {
                    state.getContext().put("finalResult", toolResult.getData());
                    state.updateStatus(AgentStatus.COMPLETED);
                    executedSteps.add(step);
                    return AgentExecutionResult.success(
                        toolResult.getData(),
                        state.getCurrentPlan(),
                        executedSteps,
                        System.currentTimeMillis() - stepStartTime);
                }
            } else {
                step.markFailed(toolResult.getError());
                memory.addShortTerm("도구 실행 실패: " + toolResult.getError(), 
                    AgentMemory.MemoryType.ERROR);
                
                if (shouldRetry(executedSteps.size())) {
                    state.updateStatus(AgentStatus.EXECUTING);
                    executedSteps.add(step);
                    return AgentExecutionResult.inProgress(
                        AgentStatus.EXECUTING,
                        state.getCurrentPlan(),
                        executedSteps,
                        System.currentTimeMillis() - stepStartTime);
                } else {
                    state.updateStatus(AgentStatus.FAILED);
                    executedSteps.add(step);
                    return AgentExecutionResult.failure(
                        "도구 실행 실패: " + toolResult.getError(),
                        state.getCurrentPlan(),
                        executedSteps,
                        System.currentTimeMillis() - stepStartTime);
                }
            }
            
            state.updateStatus(AgentStatus.EXECUTING);
            executedSteps.add(step);
            
            return AgentExecutionResult.inProgress(
                AgentStatus.EXECUTING,
                state.getCurrentPlan(),
                executedSteps,
                System.currentTimeMillis() - stepStartTime);
            
        } catch (Exception e) {
            log.error("도구 실행 중 오류 발생", e);
            state.updateStatus(AgentStatus.FAILED);
            return AgentExecutionResult.failure(
                "도구 실행 중 예외 발생: " + e.getMessage(),
                state.getCurrentPlan(),
                executedSteps,
                System.currentTimeMillis() - stepStartTime);
        }
    }
    
    private boolean shouldComplete(Object result) {
        if (result == null) {
            return false;
        }
        
        if (result instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (Map<String, Object>) result;
            
            if (resultMap.containsKey("overallRiskLevel") || 
                resultMap.containsKey("analysis") ||
                resultMap.containsKey("data")) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean shouldRetry(int failureCount) {
        return failureCount < 3;
    }
}

