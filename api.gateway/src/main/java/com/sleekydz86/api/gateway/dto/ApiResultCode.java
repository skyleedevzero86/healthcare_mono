package com.sleekydz86.api.gateway.dto;

import lombok.Getter;

@Getter
public enum ApiResultCode {
    SUCCESS("0000", "성공"),
    UNAUTHORIZED("4011", "인증이 필요합니다"),
    FORBIDDEN("4031", "접근 권한이 없습니다"),
    NOT_FOUND("4041", "리소스를 찾을 수 없습니다"),
    INTERNAL_ERROR("5001", "내부 서버 오류"),
    SERVICE_UNAVAILABLE("5031", "서비스를 사용할 수 없습니다"),
    INVALID_REQUEST("1002", "잘못된 요청"),
    RESULT_IS_EMPTY("4042", "결과가 없습니다"),
    DUPLICATE_DATA("4091", "중복된 데이터입니다");

    public final String code;
    public final String message;

    ApiResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ApiResultCode UNKOWN_ERR = INTERNAL_ERROR;
}
