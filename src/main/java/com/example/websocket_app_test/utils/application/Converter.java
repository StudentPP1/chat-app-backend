package com.example.websocket_app_test.utils.application;

import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.model.Message;
import com.example.websocket_app_test.request.MessageRequest;
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
        return new UserResponse(
                chatUser.getName(),
                chatUser.getUsername()
        );
    }
    public static MessageResponse messageConvertToResponse(Message message) {
        return new MessageResponse(
                message.getFromId(),
                message.getContent(),
                message.getTimestamp(),
                message.getChat().getId()
        );
    }
    public static MessageResponse messageConvertToResponse(MessageRequest message) {
        return new MessageResponse(
                message.getFromId(),
                message.getContent(),
                message.getTimestamp(),
                message.getChatId()
        );
    }
    public static Message requestConvertToMessage(MessageRequest message) {
        return Message.builder()
                .fromId(message.getFromId())
                .content(message.getContent())
                .timestamp(message.getTimestamp())
                .build();
    }
}
