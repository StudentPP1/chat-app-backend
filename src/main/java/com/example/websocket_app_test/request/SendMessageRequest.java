package com.example.websocket_app_test.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendMessageRequest extends MessageRequest {
    private Long chatId;
    private String fromId;
    private String content;
    private String timestamp;
}
