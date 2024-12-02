package com.example.websocket_app_test.response;

import com.example.websocket_app_test.enums.ChatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponse {
    private String chatName;
    private Long chatId;
    private List<UserResponse> users;
    private ChatType type;
    private List<MessageResponse> messages;
}