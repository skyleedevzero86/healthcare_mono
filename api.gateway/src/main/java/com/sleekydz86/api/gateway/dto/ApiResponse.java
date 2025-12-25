package com.sleekydz86.api.gateway.dto;

import com.sleekydz86.api.gateway.util.DtoConverter;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Data
@Builder
public class ApiResponse<T> {

    private String resultCode;
    private String resultMessage;
    private T resultData;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .resultCode(ApiResultCode.SUCCESS.getCode())
                .resultMessage(ApiResultCode.SUCCESS.getMessage())
                .resultData(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static String error(ApiResultCode errorCode) {
        return response(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static String error(ApiResultCode errorCode, String customMessage) {
        return response(errorCode.getCode(), customMessage, null);
    }

    public static String error(String errorCode, String message) {
        return response(errorCode, message, null);
    }

    private static String response(String code, String message, Object data) {
        ApiResponse<Object> apiResponse = ApiResponse.builder()
                .resultCode(code)
                .resultMessage(message)
                .resultData(data)
                .timestamp(LocalDateTime.now())
                .build();
        try {
            return DtoConverter.getObjectMapper().writeValueAsString(apiResponse);
        } catch (Exception e) {
            log.error("ApiResponse 직렬화 실패", e);
            return String.format(
                    "{\"resultCode\":\"%s\",\"resultMessage\":\"%s\",\"resultData\":null,\"timestamp\":null}",
                    ApiResultCode.INTERNAL_ERROR.getCode(),
                    ApiResultCode.INTERNAL_ERROR.getMessage()
            );
        }
    }
}
