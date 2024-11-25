package com.example.websocket_app_test.repository;

import com.example.websocket_app_test.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findAllByChatId(String chatId);
}
