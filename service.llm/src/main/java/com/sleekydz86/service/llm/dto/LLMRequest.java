package com.sleekydz86.service.llm.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LLMRequest {
    @NotBlank(message = "프롬프트는 필수입니다.")
    private String prompt;

    private String template;
    
    private Map<String, Object> variables;
    
    private String userId;
    
    private String conversationId;
    
    private Integer maxTokens;
    
    private Double temperature;
    
    private Double topP;
    
    private Integer topK;
    
    private Boolean stream;
    
    private String systemPrompt;
}

