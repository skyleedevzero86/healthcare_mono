package com.sleekydz86.service.healthcare.dto;

public enum ApiResultCode {

    SUCCESS					("0000", "Success"),

    PARAM_VALID_ERR			("1001", "Parameter Validation Error"),
    EXP_JWT_TOKEN_ERR		("1003", "Expired JWT Token Error"),
    INVALID_JWT_TOKEN_ERR 	("1004", "Invalid JWT Token Error"),

    DUPLICATE_KEY_ERR		("2001", "Duplicate Key Error"),
    DUPLICATE_CODE			("2002", "Duplicate Error"),

    RESULT_IS_EMPTY			("3001", "Is Empty"),
    UPDATE_FAIL				("3002","Update Fail (update row count : 0)"),
    INSERT_FAIL				("3003","insert Fail (insert row count : 0)"),
    SELECT_FAIL				("3003","insert Fail (insert row count : 0)"),

    UNKOWN_ERR				("5001", "Unknown Error");



    public String code;
    public String message;

    ApiResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }


}