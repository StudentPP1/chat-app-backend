package com.example.websocket_app_test.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateMessageRequest extends MessageRequest {
    private Long messageId;
    private Long chatId;
    private String fromId;
    private String content;
    private String type;
}
