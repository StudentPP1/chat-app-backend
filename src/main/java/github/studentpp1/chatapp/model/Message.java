package github.studentpp1.chatapp.model;

import github.studentpp1.chatapp.enums.MessageType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Message {
    @Id
    @GeneratedValue
    private Long id;
    private Date timestamp;
    @Enumerated(EnumType.STRING)
    private MessageType type;
    private String content;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private ChatRoom chatRoom;
}
