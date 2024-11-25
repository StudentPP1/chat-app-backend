package com.example.websocket_app_test.service;

import com.example.websocket_app_test.model.Message;
import com.example.websocket_app_test.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public Message save(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> findByChatId(String chatId) {
        return messageRepository.findAllByChatId(chatId);
    }
}
