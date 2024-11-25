package com.example.websocket_app_test.request;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String username;
    private String name;
    private String password;
}
