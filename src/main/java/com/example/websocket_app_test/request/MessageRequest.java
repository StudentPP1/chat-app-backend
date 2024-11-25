package com.example.websocket_app_test.request;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
    private String chatId;
    private String fromId;
    private String toId;
    private String content;
    private String timestamp;
}
