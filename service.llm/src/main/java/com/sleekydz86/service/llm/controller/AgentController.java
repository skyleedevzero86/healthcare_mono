package com.sleekydz86.service.llm.controller;

import com.sleekydz86.service.llm.application.agent.AgentOrchestrator;
import com.sleekydz86.service.llm.domain.agent.AgentExecutionResult;
import com.sleekydz86.service.llm.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {
    
    private final AgentOrchestrator agentOrchestrator;
    
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Map<String, String>>> createAgent(
            @RequestParam(required = false, defaultValue = "rule") String decisionEngine) {
        try {
            String agentId = agentOrchestrator.createAgent();
            Map<String, String> result = new HashMap<>();
            result.put("agentId", agentId);
            result.put("decisionEngine", decisionEngine);
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (Exception e) {
            log.error("Agent 생성 오류", e);
            return ResponseEntity.ok(ApiResponse.error(
                    com.sleekydz86.service.llm.dto.ApiResultCode.LLM_SERVICE_ERROR,
                    "Agent 생성 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{agentId}/execute")
    public ResponseEntity<ApiResponse<AgentExecutionResult>> execute(
            @PathVariable String agentId,
            @Valid @RequestBody AgentExecuteRequest request) {
        try {
            log.info("Agent 실행 요청: agentId={}, goal={}", agentId, request.getGoal());
            
            AgentExecutionResult result = agentOrchestrator.execute(
                    agentId, 
                    request.getGoal(), 
                    request.getContext());
            
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (IllegalArgumentException e) {
            log.error("Agent 실행 오류: Agent를 찾을 수 없음", e);
            return ResponseEntity.ok(ApiResponse.error(
                    com.sleekydz86.service.llm.dto.ApiResultCode.PARAM_VALID_ERR,
                    e.getMessage()));
        } catch (IllegalStateException e) {
            log.error("Agent 실행 오류: Agent 사용 불가", e);
            return ResponseEntity.ok(ApiResponse.error(
                    com.sleekydz86.service.llm.dto.ApiResultCode.LLM_SERVICE_ERROR,
                    e.getMessage()));
        } catch (Exception e) {
            log.error("Agent 실행 오류", e);
            return ResponseEntity.ok(ApiResponse.error(
                    com.sleekydz86.service.llm.dto.ApiResultCode.LLM_SERVICE_ERROR,
                    "Agent 실행 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{agentId}/continue")
    public ResponseEntity<ApiResponse<AgentExecutionResult>> continueExecution(
            @PathVariable String agentId) {
        try {
            AgentExecutionResult result = agentOrchestrator.continueExecution(agentId);
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (Exception e) {
            log.error("Agent 계속 실행 오류", e);
            return ResponseEntity.ok(ApiResponse.error(
                    com.sleekydz86.service.llm.dto.ApiResultCode.LLM_SERVICE_ERROR,
                    "Agent 계속 실행 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{agentId}/pause")
    public ResponseEntity<ApiResponse<Map<String, String>>> pause(@PathVariable String agentId) {
        try {
            agentOrchestrator.pauseAgent(agentId);
            Map<String, String> result = new HashMap<>();
            result.put("agentId", agentId);
            result.put("status", "paused");
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (Exception e) {
            log.error("Agent 일시정지 오류", e);
            return ResponseEntity.ok(ApiResponse.error(
                    com.sleekydz86.service.llm.dto.ApiResultCode.LLM_SERVICE_ERROR,
                    "Agent 일시정지 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    @PostMapping("/{agentId}/resume")
    public ResponseEntity<ApiResponse<Map<String, String>>> resume(@PathVariable String agentId) {
        try {
            agentOrchestrator.resumeAgent(agentId);
            Map<String, String> result = new HashMap<>();
            result.put("agentId", agentId);
            result.put("status", "resumed");
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (Exception e) {
            log.error("Agent 재개 오류", e);
            return ResponseEntity.ok(ApiResponse.error(
                    com.sleekydz86.service.llm.dto.ApiResultCode.LLM_SERVICE_ERROR,
                    "Agent 재개 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{agentId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> removeAgent(@PathVariable String agentId) {
        try {
            agentOrchestrator.removeAgent(agentId);
            Map<String, String> result = new HashMap<>();
            result.put("agentId", agentId);
            result.put("status", "removed");
            return ResponseEntity.ok(ApiResponse.ok(result));
        } catch (Exception e) {
            log.error("Agent 제거 오류", e);
            return ResponseEntity.ok(ApiResponse.error(
                    com.sleekydz86.service.llm.dto.ApiResultCode.LLM_SERVICE_ERROR,
                    "Agent 제거 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{agentId}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatus(@PathVariable String agentId) {
        try {
            Map<String, Object> status = agentOrchestrator.getAgentStatus(agentId);
            if (status == null) {
                return ResponseEntity.ok(ApiResponse.error(
                        com.sleekydz86.service.llm.dto.ApiResultCode.PARAM_VALID_ERR,
                        "Agent를 찾을 수 없습니다: " + agentId));
            }
            return ResponseEntity.ok(ApiResponse.ok(status));
        } catch (Exception e) {
            log.error("Agent 상태 조회 오류", e);
            return ResponseEntity.ok(ApiResponse.error(
                    com.sleekydz86.service.llm.dto.ApiResultCode.LLM_SERVICE_ERROR,
                    "Agent 상태 조회 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }
    
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class AgentExecuteRequest {
        private String goal;
        private Map<String, Object> context;
    }
}

