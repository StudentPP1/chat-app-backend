package com.example.websocket_app_test.controller;

import com.example.websocket_app_test.auth.utils.SecurityUtils;
import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.response.UserResponse;
import com.example.websocket_app_test.service.ChatUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final ChatUserService chatUserService;

    @GetMapping("/get/user/chats")
    public List<ChatResponse> getChats() {
        ChatUser user = SecurityUtils.getAuthenticatedUser();
        return user.getChats().stream().map(chat -> new ChatResponse(
                chat.getChatId(),
                chat.getUsers().stream().map(chatUser -> new UserResponse(
                        chatUser.getName(),
                        chatUser.getUsername()
                )).collect(Collectors.toList()),
                chat.getType()
        )).collect(Collectors.toList());
    }

    @GetMapping("/get/user/{username}")
    public List<UserResponse> getUser(@PathVariable String username) {
        return chatUserService.findAllUsersLike(username).stream()
                .map(user -> new UserResponse(user.getName(), user.getUsername()))
                .collect(Collectors.toList());
    }


}
