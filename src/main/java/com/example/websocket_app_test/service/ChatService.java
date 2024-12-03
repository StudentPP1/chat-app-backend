package com.example.websocket_app_test.service;

import com.example.websocket_app_test.enums.ChatType;
import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.model.Message;
import com.example.websocket_app_test.repository.ChatRepository;
import com.example.websocket_app_test.request.ChatCreateRequest;
import com.example.websocket_app_test.request.MessageRequest;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.response.UserResponse;
import com.example.websocket_app_test.utils.application.Converter;
import com.example.websocket_app_test.utils.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatUserService chatUserService;
    private final MessageService messageService;

    public ChatResponse getChat(Long chatId) {
        Chat chat = this.findByChatId(chatId).orElseThrow(
                () -> new ApiException("chat not found", 422)
        );
        log.info("get chat: " + chat);
        return Converter.chatConvertToResponse(chat);
    }

    @Transactional
    public ChatResponse createChat(ChatCreateRequest createRequest) {
        List<ChatUser> users = createRequest.getUsernames()
                .stream()
                .map(chatUserService::findChatUserByUsername)
                .toList();

        Chat chat = new Chat();
        chat.setChatName(createRequest.getChatName());
        chat.setType(ChatType.valueOf(createRequest.getType()));
        chat.setUsers(users);
        chat = chatRepository.save(chat);

        for (ChatUser user : users) {
            List<Chat> chats = user.getChats();
            chats.add(chat);
            user.setChats(chats);
            chatUserService.saveUser(user);
            log.info("add chat: " + chat.getChatName() + " to user: " + user.getUsername());
        }

        return Converter.chatConvertToResponse(chat);
    }
    @Transactional
    public List<UserResponse> getUsersToSend(MessageRequest messageRequest) {
        Chat chat = chatRepository.findById(messageRequest.getChatId()).orElseThrow(
                () -> new ApiException("chat not found", 500)
        );
        this.saveMessage(chat, messageRequest);
        return chat.getUsers()
                .stream()
                .filter(chatUser -> !Objects.equals(chatUser.getUsername(), messageRequest.getFromId()))
                .map(Converter::userConvertToResponse)
                .toList();
    }

    private Optional<Chat> findByChatId(Long chatId) {
        return chatRepository.findById(chatId);
    }

    private void saveMessage(Chat chat, MessageRequest messageRequest) {
        Message message = Converter.requestConvertToMessage(messageRequest);
        List<Message> messages = chat.getMessages();
        messages.add(message);
        chat.setMessages(messages);
        chat = chatRepository.save(chat);

        log.info("chat: " + chat + " after adding message: " + message);

        message.setChat(chat);
        messageService.saveMessage(message);
    }
}
