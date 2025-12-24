package com.sleekydz86.service.commu.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private String code;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(ApiResultCode.SUCCESS.code)
                .message(ApiResultCode.SUCCESS.message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok() {
        return ResponseEntity.ok(ApiResponse.<T>builder()
                .code(ApiResultCode.SUCCESS.code)
                .message(ApiResultCode.SUCCESS.message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    public static <T> ResponseEntity<ApiResponse<T>> ok(T data) {
        return ResponseEntity.ok(ApiResponse.<T>builder()
                .code(ApiResultCode.SUCCESS.code)
                .message(ApiResultCode.SUCCESS.message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build());
    }

    public static ResponseEntity<ApiResponse<?>> error(ApiResultCode errorCode) {
        return ResponseEntity.ok(ApiResponse.builder()
                .code(errorCode.code)
                .message(errorCode.message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    public static ResponseEntity<ApiResponse<?>> error(ApiResultCode errorCode, String customMessage) {
        return ResponseEntity.ok(ApiResponse.builder()
                .code(errorCode.code)
                .message(customMessage != null ? customMessage : errorCode.message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }

    public static ResponseEntity<ApiResponse<?>> error(String errorCode, String message) {
        return ResponseEntity.ok(ApiResponse.builder()
                .code(errorCode)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build());
    }
}


