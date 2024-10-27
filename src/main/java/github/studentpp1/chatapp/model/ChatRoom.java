package github.studentpp1.chatapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ChatRoom {
    @Id
    @GeneratedValue
    private Long id;
    private String chatId;

    @ManyToMany(mappedBy = "chats", fetch = FetchType.EAGER)
    private List<User> users;
    @OneToMany(mappedBy = "chatRoom")
    private List<Message> messages;
}
