package com.sleekydz86.service.llm.application.agent;

import com.sleekydz86.service.llm.domain.agent.Agent;
import com.sleekydz86.service.llm.domain.agent.AgentExecutionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class AgentOrchestrator {
    
    private final Map<String, Agent> activeAgents;
    private final HealthcareAgentFactory agentFactory;
    
    public AgentOrchestrator(HealthcareAgentFactory agentFactory) {
        this.activeAgents = new ConcurrentHashMap<>();
        this.agentFactory = agentFactory;
    }
    
    public String createAgent() {
        Agent agent = agentFactory.create();
        activeAgents.put(agent.getAgentId(), agent);
        log.debug("Agent 생성: {}", agent.getAgentId());
        return agent.getAgentId();
    }
    
    public AgentExecutionResult execute(String agentId, String goal, Map<String, Object> context) {
        Agent agent = activeAgents.get(agentId);
        if (agent == null) {
            throw new IllegalArgumentException("Agent를 찾을 수 없습니다: " + agentId);
        }
        
        if (!agent.isAvailable()) {
            throw new IllegalStateException("Agent가 사용할 수 없습니다: " + agentId);
        }
        
        log.info("Agent 실행 시작: agentId={}, goal={}", agentId, goal);
        
        AgentExecutionResult result = agent.execute(goal, context != null ? context : new HashMap<>());
        
        log.info("Agent 실행 완료: agentId={}, completed={}, status={}", 
            agentId, result.isCompleted(), result.getStatus());
        
        return result;
    }
    
    public AgentExecutionResult continueExecution(String agentId) {
        Agent agent = activeAgents.get(agentId);
        if (agent == null) {
            throw new IllegalArgumentException("Agent를 찾을 수 없습니다: " + agentId);
        }
        
        return agent.continueExecution();
    }
    
    public void pauseAgent(String agentId) {
        Agent agent = activeAgents.get(agentId);
        if (agent != null) {
            agent.pause();
            log.debug("Agent 일시정지: {}", agentId);
        }
    }
    
    public void resumeAgent(String agentId) {
        Agent agent = activeAgents.get(agentId);
        if (agent != null) {
            agent.resume();
            log.debug("Agent 재개: {}", agentId);
        }
    }
    
    public void removeAgent(String agentId) {
        Agent agent = activeAgents.remove(agentId);
        if (agent != null) {
            agent.reset();
            log.debug("Agent 제거: {}", agentId);
        }
    }
    
    public Agent getAgent(String agentId) {
        return activeAgents.get(agentId);
    }
    
    public Map<String, Object> getAgentStatus(String agentId) {
        Agent agent = activeAgents.get(agentId);
        if (agent == null) {
            return null;
        }
        
        Map<String, Object> status = new HashMap<>();
        status.put("agentId", agent.getAgentId());
        status.put("agentType", agent.getAgentType());
        status.put("status", agent.getState().getStatus());
        status.put("goal", agent.getState().getCurrentGoal());
        status.put("stepCount", agent.getState().getStepCount());
        status.put("maxSteps", agent.getState().getMaxSteps());
        status.put("available", agent.isAvailable());
        
        if (agent.getState().getCurrentPlan() != null) {
            Map<String, Object> planInfo = new HashMap<>();
            planInfo.put("planId", agent.getState().getCurrentPlan().getPlanId());
            planInfo.put("currentStepIndex", agent.getState().getCurrentPlan().getCurrentStepIndex());
            planInfo.put("totalSteps", agent.getState().getCurrentPlan().getSteps().size());
            status.put("plan", planInfo);
        }
        
        return status;
    }
}

