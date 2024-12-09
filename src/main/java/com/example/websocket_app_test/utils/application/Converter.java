package com.example.websocket_app_test.utils.application;

import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.model.Message;
import com.example.websocket_app_test.request.SendMessageRequest;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.response.MessageResponse;
import com.example.websocket_app_test.response.UserResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Converter {
    public static ChatResponse chatConvertToResponse(Chat chat) {
        List<Message> messages = chat.getMessages();
        log.info("messages from chat: " + messages);
        ChatResponse chatResponse = ChatResponse.builder()
                .chatName(chat.getChatName())
                .chatId(chat.getId())
                .users(chat.getUsers().stream().map(Converter::userConvertToResponse).toList())
                .owner(chat.getOwner())
                .type(chat.getType())
                .build();
        if (messages == null) {
            chatResponse.setMessages(new ArrayList<>());
        }
        else {
            chatResponse.setMessages(messages.stream().map(Converter::messageConvertToResponse).toList());
        }
        return chatResponse;
    }
    public static UserResponse userConvertToResponse(ChatUser chatUser) {
        return UserResponse.builder()
                .name(chatUser.getName())
                .username(chatUser.getUsername())
                .build();
    }
    public static MessageResponse messageConvertToResponse(Message message) {
        return MessageResponse.builder()
                .messageId(message.getId())
                .chatId(message.getChat().getId())
                .content(message.getContent())
                .fromId(message.getFromId())
                .type(message.getType().toString())
                .timestamp(message.getTimestamp())
                .build();
    }
    public static Message requestConvertToMessage(SendMessageRequest message) {
        return Message.builder()
                .fromId(message.getFromId())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .build();
    }
}
