package com.example.websocket_app_test.service;

import com.example.websocket_app_test.auth.utils.SecurityUtils;
import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.repository.ChatUserRepository;
import com.example.websocket_app_test.request.ChangeUserDetailsRequest;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.response.UserResponse;
import com.example.websocket_app_test.utils.application.Mapper;
import com.example.websocket_app_test.utils.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        List<Chat> chats = this.findUser(user.getUsername()).getChats();
        return chats.stream().map(Mapper::chatConvertToResponse).toList();
    }

    public void addChatToUser(ChatUser user, Chat chat) {
        List<Chat> chats = user.getChats();
        chats.add(chat);
        user.setChats(chats);
        chatUserRepository.save(user);
    }

    public void deleteUserFromChat(Chat chat) {
        for (ChatUser user : chat.getUsers()) {
            this.deleteChat(user.getUsername(), chat);
        }
    }

    public List<UserResponse> findAllUsersLike(String username) {
        ChatUser chatUser = SecurityUtils.getAuthenticatedUser();
        List<ChatUser> chatUsers = chatUserRepository.findAllByUsername(username)
                .stream()
                .filter((user) -> !Objects.equals(user.getUsername(), chatUser.getUsername()))
                .toList();
        return chatUsers.stream()
                .map(Mapper::userConvertToResponse)
                .toList();
    }

    public List<ChatUser> getChatUsers(List<String> usernames) {
        return usernames.stream()
                .map(this::findUser)
                .toList();
    }

    public void deleteChat(String username, Chat chat) {
        ChatUser user = this.findUser(username);
        List<Chat> newUserChats = user.getChats();
        newUserChats.remove(chat);
        user.setChats(newUserChats);
        chatUserRepository.save(user);
    }

    public UserResponse updateUserDetails(ChangeUserDetailsRequest request) {
        ChatUser user = SecurityUtils.getAuthenticatedUser();
        user.setUsername(request.getNewUsername());
        user.setName(request.getNewName());
        user = chatUserRepository.save(user);
        return Mapper.userConvertToResponse(user);
    }
    public UserResponse updateUserDetails(MultipartFile file) throws IOException {
        ChatUser user = SecurityUtils.getAuthenticatedUser();
        user.setImg(file.getBytes());
        user = chatUserRepository.save(user);
        return Mapper.userConvertToResponse(user);
    }

    public ChatUser findUser(String username) {
        return chatUserRepository.findByUsername(username).orElseThrow(
                () -> new ApiException("user not found", 422)
        );
    }
}
