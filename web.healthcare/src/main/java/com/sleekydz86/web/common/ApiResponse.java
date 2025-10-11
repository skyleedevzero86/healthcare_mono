package com.sleekydz86.web.common;

import org.springframework.http.HttpStatus;

public class ApiResponse<T> {

    private int status;
    private String message;
    private T data;

    public ApiResponse(HttpStatus status, String message, T data) {
        this.status = status.value();
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> of(HttpStatus status, T data) {
        String message = getDefaultMessageForStatusCode(status);
        return new ApiResponse<>(status, message, data);
    }

    private static String getDefaultMessageForStatusCode(HttpStatus status) {

        switch (status) {
            case OK:
                return "Operation succeeded";
            case CREATED:
                return "Resource created successfully";
            case NO_CONTENT:
                return "Resource deleted successfully";
            case BAD_REQUEST:
                return "Invalid request format";
            case UNAUTHORIZED:
                return "Authentication required";
            case FORBIDDEN:
                return "Access denied";
            case NOT_FOUND:
                return "Resource not found";
            case CONFLICT:
                return "Conflict detected";
            case UNPROCESSABLE_ENTITY:
                return "Unable to process the contained instructions";
            case INTERNAL_SERVER_ERROR:
                return "Internal server error occurred";
            default:
                return "HTTP Status " + status.value();
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
}
