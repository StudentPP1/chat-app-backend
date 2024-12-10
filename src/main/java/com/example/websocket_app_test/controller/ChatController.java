package com.example.websocket_app_test.controller;

import com.example.websocket_app_test.request.*;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Slf4j
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    // send messages: create new queue or update old one
    @MessageMapping("/sendMessage")
    public void sendMessage(@Payload SendMessageRequest sendMessageRequest) {
        chatService.sendMessage(sendMessageRequest);
    }

    @MessageMapping("/updateMessage")
    public void updateMessage(@Payload UpdateMessageRequest updateMessageRequest) {
        chatService.updateMessage(updateMessageRequest);
    }

    @MessageMapping("/deleteMessage")
    public void deleteMessage(@Payload DeleteMessageRequest deleteMessageRequest) {
        chatService.deleteMessage(deleteMessageRequest);
    }

    @PostMapping("/create/chat")
    public ChatResponse createChat(@RequestBody ChatCreateRequest createRequest) {
        return chatService.createChat(createRequest);
    }

    @PostMapping("/update/chat")
    public void updateChat(@RequestBody ChangeChatDetailsRequest detailsRequest) {
        chatService.changeChatDetails(detailsRequest);
    }

    @PostMapping("/update/chat/{chatId}")
    public void updateChat(
            @PathVariable(name = "chatId") Long chatId,
            @RequestParam("file") MultipartFile file) throws IOException {
        chatService.changeChatDetails(chatId, file);
    }

    @DeleteMapping("/delete/chat/")
    public void deleteChat(@RequestBody DeleteChatRequest deleteChatRequest) {
        chatService.deleteChat(
                deleteChatRequest.getFromId(),
                deleteChatRequest.getChatId()
        );
    }

    @PostMapping("/add/users/")
    public void addUsersToChat(@RequestBody AddUsersRequest addUsersRequest) {
        chatService.addUsersToChat(
                addUsersRequest.getChatId(),
                addUsersRequest.getUsernames()
        );
    }

    @GetMapping("/get/chat/{chatId}")
    public ChatResponse getChat(@PathVariable(name = "chatId") Long chatId) {
        return chatService.getChatResponse(chatId);
    }
}
