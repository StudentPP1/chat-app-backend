package com.example.websocket_app_test.utils.exception;

import lombok.Builder;


@Builder
public class ApiException extends RuntimeException {
    private String message;
    private int status;

    public ApiException(String message, int status) {
        this.message = message;
        this.status = status;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
