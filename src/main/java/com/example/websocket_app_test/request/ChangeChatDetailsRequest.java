package com.example.websocket_app_test.request;

import lombok.Data;

@Data
public class ChangeChatDetailsRequest {
    Long chatId;
    String chatName;
}
