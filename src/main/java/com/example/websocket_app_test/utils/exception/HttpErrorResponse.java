package com.example.websocket_app_test.utils.exception;

import lombok.Getter;

@Getter
public class HttpErrorResponse {
    private String statusText;
    private int status;

    public static HttpErrorResponse of(String message, int status) {
        HttpErrorResponse response = new HttpErrorResponse();
        response.statusText = message;
        response.status = status;
        return response;
    }
}