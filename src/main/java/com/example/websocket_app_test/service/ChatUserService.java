package com.example.websocket_app_test.service;

import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.repository.ChatUserRepository;
import com.example.websocket_app_test.utils.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatUserService {
    private final ChatUserRepository chatUserRepository;

    public ChatUser findChatUserByUsername(String username) {
        return chatUserRepository.findByUsername(username).orElseThrow(
                () -> new ApiException("user not found", 422)
        );
    }

    public void save(ChatUser user) {
        chatUserRepository.save(user);
    }

    public List<ChatUser> findAllUsersLike(String username) {
        return chatUserRepository.findAllByUsername(username);
    }
}
