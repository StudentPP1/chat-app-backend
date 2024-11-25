package com.example.websocket_app_test.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String chatId;

    @Column(nullable = false)
    private String fromId;

    private String toId;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String timestamp;

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", chatId='" + chatId + '\'' +
                ", fromId='" + fromId + '\'' +
                ", toId='" + toId + '\'' +
                ", content='" + content + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
