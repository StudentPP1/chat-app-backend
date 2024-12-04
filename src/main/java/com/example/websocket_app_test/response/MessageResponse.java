package com.example.websocket_app_test.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageResponse {
    private String fromId;
    private String content;
    private String timestamp;
    private Long chatId;
}
