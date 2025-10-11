package com.sleekydz86.web.common;

import com.sleekydz86.web.global.exception.ResponseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ResponseExceptionHandler {
    private static final Map<HttpStatus, String> ERROR_MAP = new HashMap<>();

    static {
        ERROR_MAP.put(HttpStatus.CONFLICT, "중복이 있습니다.");
        ERROR_MAP.put(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.");
        ERROR_MAP.put(HttpStatus.NOT_ACCEPTABLE, "지정된 형식이 아닙니다.");
    }

    @ExceptionHandler(ResponseException.class)
    public ApiResponse<String> handleResponseException(ResponseException e) {

        return ApiResponse.of(e.getStatus(), ERROR_MAP.get(e.getStatus()));
    }
}
