package com.example.websocket_app_test.model;

import com.example.websocket_app_test.enums.MessageType;
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

    @ManyToOne
    @JoinColumn(name="chat_user_id")
    private ChatUser from;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String timestamp;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name="chat_id")
    private Chat chat;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", from='" + from + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
