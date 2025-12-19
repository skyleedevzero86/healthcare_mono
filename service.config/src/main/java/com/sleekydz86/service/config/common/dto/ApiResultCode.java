package com.sleekydz86.service.config.common.dto;

public enum ApiResultCode {

    SUCCESS("0000", "성공"),
    PARAM_VALID_ERR("1001", "파라미터 검증 오류"),
    AUTH_ERR("1002", "인증 오류"),
    DUPLICATE_KEY_ERR("2001", "중복 키 오류"),
    RESULT_IS_EMPTY("3001", "결과가 비어있습니다"),
    INVALID_REQUEST("1002", "잘못된 요청"),
    UNKNOWN_ERR("5001", "알 수 없는 오류");

    public final String code;
    public final String message;

    ApiResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}

