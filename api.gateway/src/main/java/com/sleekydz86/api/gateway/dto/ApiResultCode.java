package com.sleekydz86.api.gateway.dto;

public enum ApiResultCode {

    SUCCESS                 ("0000", "성공"),
    EXP_JWT_TOKEN_ERR		("1003", "만료된 JWT 토큰 오류"),
    INVALID_JWT_TOKEN_ERR 	("1004", "유효하지 않은 JWT 토큰 오류"),
    UNAUTHORIZED            ("1005", "인증되지 않음"),
    INVALID_REQUEST         ("4001", "잘못된 요청"),
    SERVICE_UNAVAILABLE     ("5031", "서비스를 사용할 수 없음"),
    UNKOWN_ERR				("5001", "알 수 없는 오류");

    public String code;
    public String message;

    ApiResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}