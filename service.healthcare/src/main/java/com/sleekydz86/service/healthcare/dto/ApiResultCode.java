package com.sleekydz86.service.healthcare.dto;

public enum ApiResultCode {

    SUCCESS					("0000", "성공"),

    PARAM_VALID_ERR			("1001", "파라미터 검증 오류"),
    INVALID_REQUEST			("1002", "잘못된 요청"),
    EXP_JWT_TOKEN_ERR		("1003", "만료된 JWT 토큰 오류"),
    INVALID_JWT_TOKEN_ERR 	("1004", "유효하지 않은 JWT 토큰 오류"),

    DUPLICATE_KEY_ERR		("2001", "중복 키 오류"),
    DUPLICATE_CODE			("2002", "중복 오류"),

    RESULT_IS_EMPTY			("3001", "결과가 비어있습니다"),
    UPDATE_FAIL				("3002","업데이트 실패 (업데이트된 행 수 : 0)"),
    INSERT_FAIL				("3003","삽입 실패 (삽입된 행 수 : 0)"),
    SELECT_FAIL				("3004","조회 실패"),

    UNKNOWN_ERR				("5001", "알 수 없는 오류");

    public String code;
    public String message;

    ApiResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}