package com.example.websocket_app_test.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteMessageRequest extends MessageRequest {
    private Long messageId;
    private Long chatId;
    private String fromId;
    private String type;
}