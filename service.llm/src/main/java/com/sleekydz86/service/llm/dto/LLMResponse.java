package com.sleekydz86.service.llm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LLMResponse {
    private String response;
    private String conversationId;
    private Integer tokensUsed;
    private Integer promptTokens;
    private Integer completionTokens;
    private Long processingTimeMs;
    private LocalDateTime timestamp;
    private Map<String, Object> metadata;
    private List<com.sleekydz86.service.llm.dto.Message> messages;
}

