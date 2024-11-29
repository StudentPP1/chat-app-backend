package com.example.websocket_app_test.utils.application;

import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.model.Message;
import com.example.websocket_app_test.request.MessageRequest;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.response.MessageResponse;
import com.example.websocket_app_test.response.UserResponse;

public class Converter {
    public static ChatResponse chatConvertToResponse(Chat chat) {
        return new ChatResponse(
                chat.getChatId(),
                chat.getUsers().stream().map(Converter::userConvertToResponse).toList(),
                chat.getType()
        );
    }
    public static UserResponse userConvertToResponse(ChatUser chatUser) {
        return new UserResponse(
                chatUser.getName(),
                chatUser.getUsername()
        );
    }
    public static MessageResponse messageConvertToResponse(Message message) {
        return new MessageResponse(
                message.getChatId(),
                message.getFromId(),
                message.getToId(),
                message.getContent()
        );
    }
    public static MessageResponse messageConvertToResponse(MessageRequest message) {
        return new MessageResponse(
                message.getChatId(),
                message.getFromId(),
                message.getToId(),
                message.getContent()
        );
    }
}
