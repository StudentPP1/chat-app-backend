package com.example.websocket_app_test.model;

import com.example.websocket_app_test.enums.ChatType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Chat {
    @Id
    @GeneratedValue
    private Long id;
    private String chatId;
    @ManyToMany(fetch = FetchType.EAGER)
    private List<ChatUser> users;
    @Enumerated(EnumType.STRING)
    private ChatType type;
}
