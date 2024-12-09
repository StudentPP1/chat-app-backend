package com.example.websocket_app_test.request;

import lombok.Data;

@Data
public class ChangeUserDetailsRequest {
    private String newName;
    private String newUsername;
}
