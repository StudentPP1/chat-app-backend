package com.example.websocket_app_test.service;

import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    public Optional<Chat> findByChatId(String chatId) {
        return chatRepository.findByChatId(chatId);
    }

    public void save(Chat chat) {
        chatRepository.save(chat);
    }
}
