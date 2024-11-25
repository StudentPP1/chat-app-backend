package com.example.websocket_app_test.repository;

import com.example.websocket_app_test.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByChatId(String chatId);
}
