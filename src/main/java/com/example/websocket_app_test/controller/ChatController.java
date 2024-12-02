package com.example.websocket_app_test.controller;

import com.example.websocket_app_test.request.ChatCreateRequest;
import com.example.websocket_app_test.request.MessageRequest;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.response.UserResponse;
import com.example.websocket_app_test.service.ChatService;
import com.example.websocket_app_test.utils.application.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    // allow to send message to queue
    private final SimpMessagingTemplate messagingTemplate;

    // send messages: create new queue or update old one
    @MessageMapping("/chat")
    public void processMessage(@Payload MessageRequest messageRequest) {
        log.info("get message: " + messageRequest);
        List<UserResponse> users = chatService.getUsersToSend(messageRequest);
        log.info("send message to: " + Arrays.toString(users.toArray()));
        for (UserResponse user : users) {
            messagingTemplate.convertAndSendToUser(
                    user.getUsername(),"/queue/messages",
                    Converter.messageConvertToResponse(messageRequest)
            );
        }
    }

    @PostMapping("/create/chat")
    public ChatResponse createChat(@RequestBody ChatCreateRequest createRequest) {
        return chatService.createChat(createRequest);
    }

    @GetMapping("/get/chat/{chatId}")
    public ChatResponse getChat(@PathVariable(name = "chatId") Long chatId) {
        return chatService.getChat(chatId);
    }
}
