package com.example.websocket_app_test.utils.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class HttpErrorResponse {
    private String message;
    private int status;
    private Map<String, String> errors;

    public static HttpErrorResponse of(String message, int status) {
        HttpErrorResponse response = new HttpErrorResponse();
        response.message = message;
        response.status = status;
        return response;
    }

    public static HttpErrorResponse of(String message, int status, Map<String, String> errors) {
        HttpErrorResponse response = new HttpErrorResponse();
        response.message = message;
        response.status = status;
        response.errors = errors;
        return response;
    }
}
