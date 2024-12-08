package com.example.websocket_app_test.request;

import lombok.Data;

import java.util.List;

@Data
public class ChatCreateRequest {
    private String chatName;
    private List<String> usernames;
    private String type;
}
