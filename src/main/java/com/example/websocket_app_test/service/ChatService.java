package com.example.websocket_app_test.service;

import com.example.websocket_app_test.enums.ChatType;
import com.example.websocket_app_test.enums.MessageType;
import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.model.Message;
import com.example.websocket_app_test.repository.ChatRepository;
import com.example.websocket_app_test.request.*;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.response.UserResponse;
import com.example.websocket_app_test.utils.application.Converter;
import com.example.websocket_app_test.utils.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatUserService chatUserService;
    private final MessageService messageService;
    // allow to send message to queue
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void sendMessage(SendMessageRequest sendMessageRequest) {
        log.info("sending message: " + sendMessageRequest);
        Chat chat = this.getChat(sendMessageRequest.getChatId());
        Message message = this.addMessageToChat(chat, sendMessageRequest);
        this.sendMessageToUsers(getUsers(sendMessageRequest), message);
    }

    @Transactional
    public void updateMessage(UpdateMessageRequest updateMessageRequest) {
        log.info("updating message: " + updateMessageRequest);
        // update message & save
        Message message = messageService.updateMessage(updateMessageRequest);
        // send to all users, except sender
        this.sendMessageToUsers(getUsers(updateMessageRequest), message);
    }

    @Transactional
    public void deleteMessage(DeleteMessageRequest deleteMessageRequest) {
        log.info("deleting message: " + deleteMessageRequest);
        List<UserResponse> users = getUsers(deleteMessageRequest);
        Message message = messageService.deleteMessage(deleteMessageRequest);
        this.sendMessageToUsers(users, message);
    }

    @Transactional
    public ChatResponse createChat(ChatCreateRequest createRequest) {
        List<ChatUser> users = chatUserService.getChatUsers(createRequest);

        Chat chat = new Chat();
        chat.setChatName(createRequest.getChatName());
        chat.setType(ChatType.valueOf(createRequest.getType()));
        chat.setUsers(users);
        chat = chatRepository.save(chat);

        for (ChatUser user : users) {
            chatUserService.addChatToUser(user, chat);
        }

        return Converter.chatConvertToResponse(chat);
    }

    public ChatResponse getChatResponse(Long chatId) {
        Chat chat = this.findByChatId(chatId).orElseThrow(
                () -> new ApiException("chat not found", 500)
        );
        log.info("get chat: " + chat);
        return Converter.chatConvertToResponse(chat);
    }

    private List<UserResponse> getUsers(MessageRequest message) {
        Chat chat = this.getChat(message.getChatId());
        return chat.getUsers()
                .stream()
                .map(Converter::userConvertToResponse)
                .toList();
    }

    private void sendMessageToUsers(List<UserResponse> users, Message message) {
        log.info("send message to: " + Arrays.toString(users.toArray()));
        for (UserResponse user : users) {
            messagingTemplate.convertAndSendToUser(
                    user.getUsername(), "/queue/messages", Converter.messageConvertToResponse(message));
        }
    }

    private Chat getChat(Long chatId) {
        Chat chat = this.findByChatId(chatId).orElseThrow(
                () -> new ApiException("chat not found", 500)
        );
        log.info("get chat: " + chat);
        return chat;
    }

    private Optional<Chat> findByChatId(Long chatId) {
        return chatRepository.findById(chatId);
    }

    private Message addMessageToChat(Chat chat, SendMessageRequest sendMessageRequest) {
        Message message = Converter.requestConvertToMessage(sendMessageRequest);
        message.setChat(chat);
        message.setType(MessageType.SENT);
        message = messageService.saveMessage(message);

        List<Message> messages = chat.getMessages();
        messages.add(message);
        chat.setMessages(messages);
        chat = chatRepository.save(chat);

        log.info("chat: " + chat + " after adding message: " + message);

        return message;
    }
}
