package com.sleekydz86.service.usermanagement.common;

import java.util.List;

public class ServiceResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
    private String resultCode;

    private ServiceResponse(boolean success, String message, T data, List<String> errors, String resultCode) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.errors = errors;
        this.resultCode = resultCode;
    }

    public static <T> ServiceResponse<T> success(T data) {
        return new ServiceResponse<>(true, "Success", data, null, "200");
    }

    public static <T> ServiceResponse<T> success(String message, T data) {
        return new ServiceResponse<>(true, message, data, null, "200");
    }

    public static <T> ServiceResponse<T> error(String message) {
        return new ServiceResponse<>(false, message, null, null, "500");
    }

    public static <T> ServiceResponse<T> error(String message, List<String> errors) {
        return new ServiceResponse<>(false, message, null, errors, "400");
    }

    public static <T> ServiceResponse<T> error(String message, String resultCode) {
        return new ServiceResponse<>(false, message, null, null, resultCode);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }
}

