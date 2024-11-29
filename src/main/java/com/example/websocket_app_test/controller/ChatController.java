package com.example.websocket_app_test.controller;

import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.request.ChatCreateRequest;
import com.example.websocket_app_test.request.MessageRequest;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.response.MessageResponse;
import com.example.websocket_app_test.service.ChatService;
import com.example.websocket_app_test.utils.application.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

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
        Chat chat = chatService.processMessage(messageRequest);
        for (ChatUser user : chat.getUsers()) {
            messagingTemplate.convertAndSendToUser(
                    user.getUsername(),"/queue/messages",
                    Converter.messageConvertToResponse(messageRequest)
            );
        }
    }

    @PostMapping("/create/group")
    public void createGroup(@RequestBody ChatCreateRequest createRequest) {
        chatService.createGroup(createRequest);
    }

    @GetMapping("/get/messages/{chatId}")
    public List<MessageResponse> getMessages(@PathVariable(name = "chatId") String chatId) {
        return chatService.getMessages(chatId);
    }

    @GetMapping("/get/chat/{chatId}")
    public ChatResponse getChat(@PathVariable(name = "chatId") String chatId) {
        return chatService.getChat(chatId);
    }
}
