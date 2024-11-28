package com.example.websocket_app_test.service;

import com.example.websocket_app_test.enums.ChatType;
import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.model.Message;
import com.example.websocket_app_test.repository.ChatRepository;
import com.example.websocket_app_test.request.ChatCreateRequest;
import com.example.websocket_app_test.request.MessageRequest;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.response.MessageResponse;
import com.example.websocket_app_test.response.UserResponse;
import com.example.websocket_app_test.utils.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatUserService chatUserService;
    private final MessageService messageService;

    public Optional<Chat> findByChatId(String chatId) {
        return chatRepository.findByChatId(chatId);
    }

    public void save(Chat chat) {
        chatRepository.save(chat);
    }

    public ChatResponse getChat(String chatId) {
        Chat chat = this.findByChatId(chatId).orElseThrow(
                () -> new ApiException("chat not found", 422));
        return new ChatResponse(
                chat.getChatId(),
                chat.getUsers().stream().map(
                        user -> new UserResponse(user.getName(), user.getUsername())
                ).collect(Collectors.toList()),
                chat.getType()
        );
    }

    public List<MessageResponse> getMessages(String chatId) {
        return messageService.findByChatId(chatId).stream().map(
                message -> new MessageResponse(
                        message.getChatId(),
                        message.getFromId(),
                        message.getToId(),
                        message.getContent()
                )
        ).collect(Collectors.toList());
    }

    @Transactional
    public void createGroup(ChatCreateRequest createRequest) {
        List<ChatUser> users = createRequest.getUsernames()
                .stream().map(chatUserService::findChatUserByUsername).toList();

        Chat chat = new Chat();
        chat.setChatId(createRequest.getChatId());
        chat.setUsers(users);
        this.save(chat);

        for (ChatUser user : users) {
            user.addChat(chat);
            chatUserService.save(user);
        }
    }

    @Transactional
    public Chat processMessage(MessageRequest messageRequest) {
        Message message = messageService.save(Message.builder()
                .chatId(messageRequest.getChatId())
                .content(messageRequest.getContent())
                .fromId(messageRequest.getFromId())
                .toId(messageRequest.getToId())
                .timestamp(messageRequest.getTimestamp())
                .build());
        Optional<Chat> optionalChat = this.findByChatId(message.getChatId());
        return optionalChat.orElseGet(() -> createChat(message));
    }

    private Chat createChat(Message message) {
        Chat chat = new Chat();
        chat.setChatId(message.getChatId());
        chat.setType(ChatType.PERSONAL);
        ChatUser from = chatUserService.findChatUserByUsername(message.getFromId());
        ChatUser to = chatUserService.findChatUserByUsername(message.getToId());
        chat.setUsers(List.of(from, to));
        this.save(chat);

        from.addChat(chat);
        to.addChat(chat);
        chatUserService.save(from);
        chatUserService.save(to);

        return chat;
    }
}
