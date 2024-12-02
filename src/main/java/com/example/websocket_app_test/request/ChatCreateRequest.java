package com.example.websocket_app_test.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatCreateRequest {
    private String chatName;
    private List<String> usernames;
    private String type;
}
