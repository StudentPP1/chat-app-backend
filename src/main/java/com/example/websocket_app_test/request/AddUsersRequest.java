package com.example.websocket_app_test.request;

import lombok.Data;

import java.util.List;

@Data
public class AddUsersRequest {
    private Long chatId;
    private List<String> usernames;
}
