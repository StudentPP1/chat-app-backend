package com.example.websocket_app_test.repository;

import com.example.websocket_app_test.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Long> {
}
