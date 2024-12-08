package com.example.websocket_app_test.response;

import com.example.websocket_app_test.enums.ChatType;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ChatResponse {
    private String chatName;
    private Long chatId;
    private List<UserResponse> users;
    private ChatType type;
    private List<MessageResponse> messages;
}