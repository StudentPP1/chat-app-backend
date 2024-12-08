package com.example.websocket_app_test.repository;

import com.example.websocket_app_test.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MessageRepository extends JpaRepository<Message, Long> {
}
