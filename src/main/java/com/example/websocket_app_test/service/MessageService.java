package com.example.websocket_app_test.service;

import com.example.websocket_app_test.enums.MessageType;
import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.model.Message;
import com.example.websocket_app_test.repository.MessageRepository;
import com.example.websocket_app_test.request.DeleteMessageRequest;
import com.example.websocket_app_test.request.UpdateMessageRequest;
import com.example.websocket_app_test.utils.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    public Message updateMessage(UpdateMessageRequest request) {
        Message message = this.getMessage(request.getMessageId());
        message.setType(MessageType.UPDATE);
        message.setContent(request.getContent());
        return messageRepository.save(message);
    }
    public Message deleteMessage(DeleteMessageRequest deleteMessageRequest) {
        Message message = this.getMessage(deleteMessageRequest.getMessageId());
        message.setType(MessageType.DELETE);
        messageRepository.deleteById(deleteMessageRequest.getMessageId());
        return message;
    }

    public void deleteMessageByChat(Chat chat) {
        messageRepository.deleteAll(chat.getMessages());
    }

    public Message saveMessage(Message message) { return messageRepository.save(message); }
    private Message getMessage(Long id) { return messageRepository.findById(id)
            .orElseThrow(() -> new ApiException("message not found", 422)); }
}
