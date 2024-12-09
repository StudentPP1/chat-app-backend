package com.example.websocket_app_test.controller;

import com.example.websocket_app_test.request.ChangeUserDetailsRequest;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.response.UserResponse;
import com.example.websocket_app_test.service.ChatUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final ChatUserService chatUserService;

    @GetMapping("/get/user/chats")
    public List<ChatResponse> getChats() {
        return chatUserService.getChats();
    }

    @GetMapping("/get/user/{username}")
    public List<UserResponse> getUser(@PathVariable String username) {
        return chatUserService.findAllUsersLike(username);
    }

    @PostMapping("/update/user")
    public UserResponse updateUser(@RequestBody ChangeUserDetailsRequest request) {
        return chatUserService.updateUserDetails(request);
    }
}
