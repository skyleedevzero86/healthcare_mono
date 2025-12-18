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

    private String template; // 프롬프트 템플릿 이름 (healthcare, general 등)
    
    private Map<String, Object> variables; // 템플릿 변수
    
    private String userId;
    
    private String conversationId; // 대화 세션 ID
    
    private Integer maxTokens; // 최대 토큰 수 (기본값: 설정값 사용)
    
    private Double temperature; // 온도 (기본값: 설정값 사용)
    
    private Double topP; // Top-P (기본값: 설정값 사용)
    
    private Integer topK; // Top-K (기본값: 설정값 사용)
    
    private Boolean stream; // 스트리밍 응답 여부
    
    private String systemPrompt; // 시스템 프롬프트 오버라이드
}

