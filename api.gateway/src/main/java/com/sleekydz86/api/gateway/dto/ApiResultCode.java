package com.sleekydz86.api.gateway.dto;

public enum ApiResultCode {


    EXP_JWT_TOKEN_ERR		("1003", "Expired JWT Token Error"),
    INVALID_JWT_TOKEN_ERR 	("1004", "Invalid JWT Token Error"),
    UNKOWN_ERR				("5001", "Unknown Error");



    public String code;
    public String message;

    ApiResultCode(String code, String message) {
        this.code = code;
        this.message = message;
    }


}