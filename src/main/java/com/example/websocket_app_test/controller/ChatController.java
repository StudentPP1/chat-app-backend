package com.example.websocket_app_test.controller;

import com.example.websocket_app_test.enums.ChatType;
import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.model.Message;
import com.example.websocket_app_test.request.ChatCreateRequest;
import com.example.websocket_app_test.request.MessageRequest;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.response.MessageResponse;
import com.example.websocket_app_test.response.UserResponse;
import com.example.websocket_app_test.service.ChatService;
import com.example.websocket_app_test.service.ChatUserService;
import com.example.websocket_app_test.service.MessageService;
import com.example.websocket_app_test.utils.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final MessageService messageService;
    private final ChatUserService chatUserService;
    // allow to send message to queue
    private final SimpMessagingTemplate messagingTemplate;

    // send messages: create new queue or update old one
    @MessageMapping("/chat")
    public void processMessage(@Payload MessageRequest messageRequest) {
        log.info("get message: " + messageRequest);
        Message message = messageService.save(Message.builder()
                .chatId(messageRequest.getChatId())
                .content(messageRequest.getContent())
                .fromId(messageRequest.getFromId())
                .toId(messageRequest.getToId())
                .timestamp(messageRequest.getTimestamp())
                .build());
        Optional<Chat> optionalChat = chatService.findByChatId(message.getChatId());
        Chat chat = optionalChat.orElseGet(() -> createChat(message));
        for (ChatUser user : chat.getUsers()) {
            messagingTemplate.convertAndSendToUser(
                    user.getUsername(),"/queue/messages",
                    new MessageResponse(
                            message.getChatId(),
                            message.getFromId(),
                            message.getToId(),
                            message.getContent()
                    )
            );
        }
    }

    @PostMapping("/create/group")
    public void createGroup(@RequestBody ChatCreateRequest createRequest) {
        List<ChatUser> users = createRequest.getUsernames()
                .stream().map(chatUserService::findChatUserByUsername).toList();
        Chat chat = new Chat();
        chat.setChatId(createRequest.getChatId());
        chat.setUsers(users);
        chatService.save(chat);
        for (ChatUser user : users) {
            user.addChat(chat);
            chatUserService.save(user);
        }
    }

    @GetMapping("/get/messages")
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

    @GetMapping("/get/chat")
    public ChatResponse getChat(String chatId) {
        Chat chat = chatService.findByChatId(chatId).orElseThrow(
                () -> new ApiException("chat not found", 422));
        return new ChatResponse(
                chat.getChatId(),
                chat.getUsers().stream().map(
                        user -> new UserResponse(user.getName(), user.getUsername())
                ).collect(Collectors.toList()),
                chat.getType()
        );
    }

    private Chat createChat(Message message) {
        Chat chat = new Chat();
        chat.setChatId(message.getChatId());
        chat.setType(ChatType.PERSONAL);
        ChatUser from = chatUserService.findChatUserByUsername(message.getFromId());
        ChatUser to = chatUserService.findChatUserByUsername(message.getToId());
        chat.setUsers(List.of(from, to));
        chatService.save(chat);

        from.addChat(chat);
        to.addChat(chat);
        chatUserService.save(from);
        chatUserService.save(to);

        return chat;
    }
}
