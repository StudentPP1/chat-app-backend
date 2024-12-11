package com.example.websocket_app_test.model;

import com.example.websocket_app_test.enums.ChatType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Entity
public class Chat {
    @Id
    @GeneratedValue
    private Long id;

    @Setter
    private String chatName;

    @Setter
    private String owner;

    @Setter
    private byte[] img;

    @Setter
    @Enumerated(EnumType.STRING)
    private ChatType type;

    @Setter
    @ManyToMany(fetch = FetchType.EAGER)
    private List<ChatUser> users;

    @Setter
    @OneToMany(mappedBy = "chat", fetch = FetchType.EAGER)
    @OrderBy("timestamp")
    private List<Message> messages;
}
