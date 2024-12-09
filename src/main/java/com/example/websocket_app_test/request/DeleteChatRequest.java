package com.example.websocket_app_test.request;

import lombok.Data;

@Data
public class DeleteChatRequest {
    private String fromId;
    private Long chatId;
}
