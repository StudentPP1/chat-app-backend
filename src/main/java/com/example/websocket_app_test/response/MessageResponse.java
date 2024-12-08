package com.example.websocket_app_test.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MessageResponse {
    private Long messageId;
    private Long chatId;
    private String content;
    private String fromId;
    private String type;
    private String timestamp;
}
