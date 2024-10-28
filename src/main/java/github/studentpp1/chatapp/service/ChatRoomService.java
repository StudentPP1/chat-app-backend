package github.studentpp1.chatapp.service;

import github.studentpp1.chatapp.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository repository;

    public void createChatRoom(String name, List<String> userNames) {

    }
}
