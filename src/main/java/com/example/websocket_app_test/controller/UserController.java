package com.example.websocket_app_test.controller;

import com.example.websocket_app_test.auth.utils.SecurityUtils;
import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.response.UserResponse;
import com.example.websocket_app_test.service.ChatUserService;
import com.example.websocket_app_test.utils.application.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final ChatUserService chatUserService;

    @GetMapping("/get/user/chats")
    public List<ChatResponse> getChats() {
        ChatUser user = SecurityUtils.getAuthenticatedUser();
        List<Chat> chats = chatUserService.findChatUserByUsername(user.getUsername()).getChats();
        log.info("chats from user: " + Arrays.toString(chats.toArray()));
        if (chats != null) {
            return chats.stream().map(Converter::chatConvertToResponse).toList();
        }
        else {
            return new ArrayList<>();
        }
    }

    @GetMapping("/get/user/{username}")
    public List<UserResponse> getUser(@PathVariable String username) {
        return chatUserService.findAllUsersLike(username).stream()
                .map(Converter::userConvertToResponse)
                .collect(Collectors.toList());
    }
}
