package com.example.websocket_app_test.utils.exception;

import lombok.Builder;
import lombok.Getter;


@Builder
public class ApiException extends RuntimeException {
    private String message;
    @Getter
    private int status;

    public ApiException(String message, int status) {
        this.message = message;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
