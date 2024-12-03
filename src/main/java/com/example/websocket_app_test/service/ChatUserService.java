package com.example.websocket_app_test.service;

import com.example.websocket_app_test.auth.utils.SecurityUtils;
import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.repository.ChatUserRepository;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.utils.application.Converter;
import com.example.websocket_app_test.utils.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatUserService {
    private final ChatUserRepository chatUserRepository;

    @Transactional
    public List<ChatResponse> getChats() {
        ChatUser user = SecurityUtils.getAuthenticatedUser();
        List<Chat> chats = this.findChatUserByUsername(user.getUsername()).getChats();
        log.info("chats: " + Arrays.toString(chats.toArray()) + " from user: " + user.getUsername());
        if (chats != null) {
            return chats.stream().map(Converter::chatConvertToResponse).toList();
        }
        else {
            return new ArrayList<>();
        }
    }

    public ChatUser findChatUserByUsername(String username) {
        return chatUserRepository.findByUsername(username).orElseThrow(
                () -> new ApiException("user not found", 422)
        );
    }

    public void saveUser(ChatUser user) {
        chatUserRepository.save(user);
    }

    public List<ChatUser> findAllUsersLike(String username) {
        ChatUser chatUser = SecurityUtils.getAuthenticatedUser();
        return chatUserRepository.findAllByUsername(username)
                .stream()
                .filter((user) -> !Objects.equals(user.getUsername(), chatUser.getUsername()))
                .collect(Collectors.toList());
    }
}
