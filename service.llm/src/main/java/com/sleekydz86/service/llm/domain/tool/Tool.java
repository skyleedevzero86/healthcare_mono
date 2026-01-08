package com.sleekydz86.service.llm.domain.tool;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

public interface Tool {
    String getName();
    String getDescription();
    ToolSchema getSchema();
    ToolResult execute(Map<String, Object> input);
    boolean canHandle(String action);
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ToolSchema {
        private String name;
        private String description;
        private Map<String, ParameterSchema> parameters;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ParameterSchema {
        private String type;
        private String description;
        private boolean required;
        private Object defaultValue;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    class ToolResult {
        private boolean success;
        private Object data;
        private String error;
        private long executionTimeMs;
        
        public static ToolResult success(Object data, long executionTimeMs) {
            return ToolResult.builder()
                    .success(true)
                    .data(data)
                    .executionTimeMs(executionTimeMs)
                    .build();
        }
        
        public static ToolResult failure(String error, long executionTimeMs) {
            return ToolResult.builder()
                    .success(false)
                    .error(error)
                    .executionTimeMs(executionTimeMs)
                    .build();
        }
    }
}

