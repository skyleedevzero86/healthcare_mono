package com.sleekydz86.service.llm.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiResultCode {
    SUCCESS("SUCCESS", "요청이 성공적으로 처리되었습니다."),
    INVALID_REQUEST("INVALID_REQUEST", "잘못된 요청입니다."),
    UNAUTHORIZED("UNAUTHORIZED", "인증이 필요합니다."),
    FORBIDDEN("FORBIDDEN", "접근 권한이 없습니다."),
    NOT_FOUND("NOT_FOUND", "요청한 리소스를 찾을 수 없습니다."),
    LLM_SERVICE_ERROR("LLM_SERVICE_ERROR", "LLM 서비스 오류가 발생했습니다."),
    LLM_TIMEOUT("LLM_TIMEOUT", "LLM 응답 시간이 초과되었습니다."),
    LLM_RATE_LIMIT_EXCEEDED("LLM_RATE_LIMIT_EXCEEDED", "요청 한도를 초과했습니다."),
    INVALID_PROMPT("INVALID_PROMPT", "유효하지 않은 프롬프트입니다."),
    CACHE_ERROR("CACHE_ERROR", "캐시 처리 중 오류가 발생했습니다."),
    UNKNOWN_ERR("UNKNOWN_ERR", "알 수 없는 오류가 발생했습니다.");

    private final String code;
    private final String message;
}

