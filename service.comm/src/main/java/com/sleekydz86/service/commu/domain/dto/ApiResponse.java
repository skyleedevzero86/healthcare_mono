package com.sleekydz86.service.commu.domain.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data @Builder
public class ApiResponse {

    private String resultCode;
    private String resultMessage;
    private Object resultData;


    public static ResponseEntity<ApiResponse> ok() {
        return response(ApiResultCode.SUCCESS.code, ApiResultCode.SUCCESS.message, null);
    }

    public static ResponseEntity<ApiResponse> ok(Object data) {
        return response(ApiResultCode.SUCCESS.code, ApiResultCode.SUCCESS.message, data);
    }

    public static ResponseEntity<ApiResponse> error(ApiResultCode errorCode) {
        return response(errorCode.code, errorCode.message, null);
    }


    private static ResponseEntity<ApiResponse> response(String code, String message, Object data) {
        return ResponseEntity.ok(ApiResponse.builder()
                .resultCode(code)
                .resultMessage(message)
                .resultData(data)
                .build()
        );
    }
}