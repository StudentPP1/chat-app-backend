package com.example.websocket_app_test.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    private Long chatId;
    private String fromId;
    private String content;
    private String timestamp;
}
