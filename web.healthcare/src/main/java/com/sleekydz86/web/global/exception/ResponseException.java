package com.sleekydz86.web.global.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class ResponseException extends RuntimeException {
    private String message;
    private HttpStatus status;
    private String hint;

    public ResponseException(HttpStatus status) {
        this.status = status;
    }

    public ResponseException(HttpStatus resultCode, String message, String hint) {
        this.message = message;
        this.hint = hint;
    }

    public ResponseException(String message) {
        super(message);
    }

    public ResponseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResponseException(Throwable cause) {
        super(cause);
    }
}
