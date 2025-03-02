package com.example.websocket_app_test.service;

import com.example.websocket_app_test.enums.ChatType;
import com.example.websocket_app_test.enums.MessageType;
import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.repository.ChatRepository;
import com.example.websocket_app_test.request.*;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.response.MessageResponse;
import com.example.websocket_app_test.response.UserResponse;
import com.example.websocket_app_test.utils.application.Mapper;
import com.example.websocket_app_test.utils.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatUserService chatUserService;
    private final MessageService messageService;
    // allow to send message to queue
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public ChatResponse createChat(ChatCreateRequest request) {
        List<ChatUser> users = chatUserService.getChatUsers(request.getUsernames());

        log.info(users.stream().map(ChatUser::getUsername).toString());

        Chat chat = new Chat();
        chat.setChatName(request.getChatName());
        chat.setOwner(request.getOwner());
        chat.setType(ChatType.valueOf(request.getType()));
        chat.setUsers(users);
        chat = chatRepository.save(chat);

        log.info(String.valueOf(chat));

        for (ChatUser user : users) {
            chatUserService.addChatToUser(user, chat);
        }

        return Mapper.chatConvertToResponse(chat);
    }

    @Transactional
    public void deleteChat(String fromId, Long chatId) {
        Chat chat = this.getChat(chatId);
        List<UserResponse> users = chat.getUsers().stream()
                .map(Mapper::userConvertToResponse)
                .toList();

        if (chat.getType().equals(ChatType.PERSONAL)) {
            this.deleteChat(chat);
        }
        else {
           this.deleteGroup(chat, fromId);
        }

        this.sendMessageToUsers(
                users,
                MessageResponse.builder().type(String.valueOf(MessageType.SYSTEM)).build()
        );
    }

    @Transactional
    public void addUsersToChat(Long chatId, List<String> usernames) {
        List<ChatUser> newUsers = chatUserService.getChatUsers(usernames);

        Chat chat = this.getChat(chatId);
        List<ChatUser> users = chat.getUsers();
        users.addAll(newUsers);
        chat.setUsers(users);
        chat = chatRepository.save(chat);

        for (ChatUser user : newUsers) {
            chatUserService.addChatToUser(user, chat);
        }

        this.sendMessageToUsers(
                chat.getUsers().stream().map(Mapper::userConvertToResponse).toList(),
                MessageResponse.builder().type(String.valueOf(MessageType.SYSTEM)).build()
        );
    }

    public ChatResponse getChatResponse(Long chatId) {
        return Mapper.chatConvertToResponse(this.getChat(chatId));
    }

    public void changeChatDetails(ChangeChatDetailsRequest request) {
        Chat chat = this.getChat(request.getChatId());
        chat.setChatName(request.getChatName());
        chat = chatRepository.save(chat);
        this.sendMessageToUsers(
                chat.getUsers().stream().map(Mapper::userConvertToResponse).toList(),
                MessageResponse.builder().type(String.valueOf(MessageType.SYSTEM)).build()
        );
    }

    public void changeChatDetails(Long chatId, MultipartFile file) throws IOException {
        Chat chat = this.getChat(chatId);
        chat.setImg(file.getBytes());
        chat = chatRepository.save(chat);
        this.sendMessageToUsers(
                chat.getUsers().stream().map(Mapper::userConvertToResponse).toList(),
                MessageResponse.builder().type(String.valueOf(MessageType.SYSTEM)).build()
        );
    }

    public void sendMessageToUsers(List<UserResponse> users, MessageResponse message) {
        for (UserResponse user : users) {
            messagingTemplate.convertAndSendToUser(
                    user.getUsername(), "/queue/messages", message);
        }
    }

    public void saveChat(Chat chat) {
        chatRepository.save(chat);
    }

    public Chat getChat(Long chatId) {
        return chatRepository.findById(chatId).orElseThrow(
                () -> new ApiException("chat not found", 500)
        );
    }

    private void deleteGroup(Chat chat, String fromId) {
        if (Objects.equals(chat.getOwner(), fromId)) {
            this.deleteChat(chat);
        }
        else {
            List<ChatUser> chatUsers = chat.getUsers();
            chatUsers.remove(chatUserService.findUser(fromId));
            chat.setUsers(chatUsers);
            chat = chatRepository.save(chat);
            chatUserService.deleteChat(fromId, chat);
        }
    }

    private void deleteChat(Chat chat) {
        chatUserService.deleteUserFromChat(chat);
        messageService.deleteMessageByChat(chat);
        chatRepository.delete(chat);
    }
}
