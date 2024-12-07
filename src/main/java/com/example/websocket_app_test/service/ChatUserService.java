package com.example.websocket_app_test.service;

import com.example.websocket_app_test.auth.utils.SecurityUtils;
import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.repository.ChatUserRepository;
import com.example.websocket_app_test.request.ChatCreateRequest;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.response.UserResponse;
import com.example.websocket_app_test.utils.application.Converter;
import com.example.websocket_app_test.utils.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
        return chats.stream().map(Converter::chatConvertToResponse).toList();
    }

    public void addChatToUser(ChatUser user, Chat chat) {
        List<Chat> chats = user.getChats();
        chats.add(chat);
        user.setChats(chats);
        this.saveUser(user);
        log.info("add chat: " + chat.getChatName() + " to user: " + user.getUsername());
    }

    public List<UserResponse> findAllUsersLike(String username) {
        ChatUser chatUser = SecurityUtils.getAuthenticatedUser();
        List<ChatUser> chatUsers = chatUserRepository.findAllByUsername(username)
                .stream()
                .filter((user) -> !Objects.equals(user.getUsername(), chatUser.getUsername()))
                .toList();
        return chatUsers.stream()
                .map(Converter::userConvertToResponse)
                .toList();
    }

    public List<ChatUser> getChatUsers(ChatCreateRequest createRequest) {
        return createRequest.getUsernames()
                .stream()
                .map(this::findChatUserByUsername)
                .toList();
    }

    private void saveUser(ChatUser user) {
        chatUserRepository.save(user);
    }

    private ChatUser findChatUserByUsername(String username) {
        return chatUserRepository.findByUsername(username).orElseThrow(
                () -> new ApiException("user not found", 422)
        );
    }
}
