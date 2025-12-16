package com.sleekydz86.web.common;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public ApiResponse(HttpStatus status, String message, T data) {
        this.status = status.value();
        this.message = message;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK, "성공", data);
    }

    public static <T> ApiResponse<T> of(HttpStatus status, T data) {
        String message = getDefaultMessageForStatusCode(status);
        return new ApiResponse<>(status, message, data);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
    }

    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR, message, null);
    }

    private static String getDefaultMessageForStatusCode(HttpStatus status) {
        switch (status) {
            case OK:
                return "작업이 성공적으로 완료되었습니다.";
            case CREATED:
                return "리소스가 성공적으로 생성되었습니다.";
            case NO_CONTENT:
                return "리소스가 성공적으로 삭제되었습니다.";
            case BAD_REQUEST:
                return "잘못된 요청 형식입니다.";
            case UNAUTHORIZED:
                return "인증이 필요합니다.";
            case FORBIDDEN:
                return "접근이 거부되었습니다.";
            case NOT_FOUND:
                return "리소스를 찾을 수 없습니다.";
            case CONFLICT:
                return "충돌이 감지되었습니다.";
            case UNPROCESSABLE_ENTITY:
                return "포함된 지시사항을 처리할 수 없습니다.";
            case INTERNAL_SERVER_ERROR:
                return "내부 서버 오류가 발생했습니다.";
            default:
                return "HTTP 상태 코드 " + status.value();
        }
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
