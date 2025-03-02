package com.example.websocket_app_test.service;

import com.example.websocket_app_test.enums.MessageType;
import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.model.Message;
import com.example.websocket_app_test.request.DeleteMessageRequest;
import com.example.websocket_app_test.request.MessageRequest;
import com.example.websocket_app_test.request.SendMessageRequest;
import com.example.websocket_app_test.request.UpdateMessageRequest;
import com.example.websocket_app_test.response.UserResponse;
import com.example.websocket_app_test.utils.application.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final ChatService chatService;
    private final MessageService messageService;
    private final ChatUserService userService;

    @Transactional
    public void sendMessage(SendMessageRequest request) {
        Chat chat = chatService.getChat(request.getChatId());
        Message message = this.addMessageToChat(chat, request);
        chatService.sendMessageToUsers(
                this.getUsers(request),
                Mapper.messageConvertToResponse(message)
        );
    }

    @Transactional
    public void updateMessage(UpdateMessageRequest request) {
        // update message & save
        Message message = messageService.updateMessage(request);
        // send to all users, except sender
        chatService.sendMessageToUsers(
                this.getUsers(request),
                Mapper.messageConvertToResponse(message)
        );
    }

    @Transactional
    public void deleteMessage(DeleteMessageRequest request) {
        Message message = messageService.deleteMessage(request);
        chatService.sendMessageToUsers(
                this.getUsers(request),
                Mapper.messageConvertToResponse(message)
        );
    }

    private Message addMessageToChat(Chat chat, SendMessageRequest request) {
        Message message = Mapper.requestConvertToMessage(request);
        message.setFrom(userService.findUser(request.getFromId()));
        message.setChat(chat);
        message.setType(MessageType.SENT);
        message = messageService.saveMessage(message);

        List<Message> messages = chat.getMessages();
        messages.add(message);
        chat.setMessages(messages);
        chatService.saveChat(chat);

        return message;
    }

    private List<UserResponse> getUsers(MessageRequest message) {
        Chat chat = chatService.getChat(message.getChatId());
        return chat.getUsers()
                .stream()
                .map(Mapper::userConvertToResponse)
                .toList();
    }
}
