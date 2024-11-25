package com.example.websocket_app_test.request;

import lombok.Data;

@Data
public class UserLoginRequest {
    private String username;
    private String password;
}
