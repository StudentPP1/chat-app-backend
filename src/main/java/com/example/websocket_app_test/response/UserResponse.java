package com.example.websocket_app_test.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponse {
    private String name;
    private String username;
    private byte[] img;
}
