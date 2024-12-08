package com.example.websocket_app_test.request;

import lombok.Data;

@Data
public abstract class MessageRequest {
    Long chatId;
    String fromId;
}
