package com.sleekydz86.service.llm.infrastructure.adapter.tool;

import com.sleekydz86.service.llm.domain.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class GetHealthDataTool implements Tool {
    
    @Override
    public String getName() {
        return "get_health_data";
    }
    
    @Override
    public String getDescription() {
        return "사용자의 건강 데이터를 조회합니다. 최근 건강 정보, 건강 점수, 차트 데이터 등을 가져올 수 있습니다.";
    }
    
    @Override
    public ToolSchema getSchema() {
        Map<String, ParameterSchema> parameters = new HashMap<>();
        parameters.put("userId", ParameterSchema.builder()
                .type("string")
                .description("사용자 ID")
                .required(true)
                .build());
        parameters.put("dataType", ParameterSchema.builder()
                .type("string")
                .description("데이터 타입: recent, score, chart")
                .required(false)
                .defaultValue("recent")
                .build());
        parameters.put("startDate", ParameterSchema.builder()
                .type("string")
                .description("시작 날짜 (YYYY-MM-DD)")
                .required(false)
                .build());
        parameters.put("endDate", ParameterSchema.builder()
                .type("string")
                .description("종료 날짜 (YYYY-MM-DD)")
                .required(false)
                .build());
        
        return ToolSchema.builder()
                .name(getName())
                .description(getDescription())
                .parameters(parameters)
                .build();
    }
    
    @Override
    public ToolResult execute(Map<String, Object> input) {
        long startTime = System.currentTimeMillis();
        
        try {
            if (input == null || input.isEmpty()) {
                return ToolResult.failure("입력 데이터가 필요합니다.", System.currentTimeMillis() - startTime);
            }
            
            String userId = (String) input.get("userId");
            if (userId == null || userId.isEmpty()) {
                return ToolResult.failure("사용자 ID가 필요합니다.", System.currentTimeMillis() - startTime);
            }
            
            String dataType = (String) input.getOrDefault("dataType", "recent");
            
            Map<String, Object> request = new HashMap<>();
            request.put("userId", userId);
            
            if (input.get("startDate") != null) {
                request.put("startDate", input.get("startDate"));
            }
            if (input.get("endDate") != null) {
                request.put("endDate", input.get("endDate"));
            }
            
            Map<String, Object> result = new HashMap<>();
            
            switch (dataType.toLowerCase()) {
                case "recent":
                    result = fetchRecentHealthData(userId);
                    break;
                case "score":
                    result = fetchHealthScore(userId);
                    break;
                case "chart":
                    result = fetchHealthChart(request);
                    break;
                default:
                    result = fetchRecentHealthData(userId);
            }
            
            return ToolResult.success(result, System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            log.error("건강 데이터 조회 도구 실행 오류", e);
            return ToolResult.failure("건강 데이터 조회 중 오류가 발생했습니다: " + e.getMessage(),
                    System.currentTimeMillis() - startTime);
        }
    }
    
    @Override
    public boolean canHandle(String action) {
        return action != null && (
            action.toLowerCase().contains("get") ||
            action.toLowerCase().contains("조회") ||
            action.toLowerCase().contains("fetch") ||
            action.toLowerCase().contains("data") ||
            action.toLowerCase().contains("데이터")
        );
    }
    
    private Map<String, Object> fetchRecentHealthData(String userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("dataType", "recent");
        result.put("userId", userId);
        result.put("message", "건강 데이터 조회 기능은 Healthcare 서비스와의 통합이 필요합니다.");
        return result;
    }
    
    private Map<String, Object> fetchHealthScore(String userId) {
        Map<String, Object> result = new HashMap<>();
        result.put("dataType", "score");
        result.put("userId", userId);
        result.put("message", "건강 점수 조회 기능은 Healthcare 서비스와의 통합이 필요합니다.");
        return result;
    }
    
    private Map<String, Object> fetchHealthChart(Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        result.put("dataType", "chart");
        result.putAll(request);
        result.put("message", "건강 차트 조회 기능은 Healthcare 서비스와의 통합이 필요합니다.");
        return result;
    }
}

