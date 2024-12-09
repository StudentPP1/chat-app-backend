package com.example.websocket_app_test.service;

import com.example.websocket_app_test.enums.ChatType;
import com.example.websocket_app_test.enums.MessageType;
import com.example.websocket_app_test.model.Chat;
import com.example.websocket_app_test.model.ChatUser;
import com.example.websocket_app_test.model.Message;
import com.example.websocket_app_test.repository.ChatRepository;
import com.example.websocket_app_test.request.*;
import com.example.websocket_app_test.response.ChatResponse;
import com.example.websocket_app_test.response.MessageResponse;
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
import java.util.Objects;

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
        this.sendMessageToUsers(
                chat.getUsers().stream().map(Converter::userConvertToResponse).toList(),
                Converter.messageConvertToResponse(message)
        );
    }

    @Transactional
    public void updateMessage(UpdateMessageRequest updateMessageRequest) {
        log.info("updating message: " + updateMessageRequest);
        // update message & save
        Message message = messageService.updateMessage(updateMessageRequest);
        // send to all users, except sender
        this.sendMessageToUsers(
                getUsers(updateMessageRequest),
                Converter.messageConvertToResponse(message)
        );
    }

    @Transactional
    public void deleteMessage(DeleteMessageRequest deleteMessageRequest) {
        log.info("deleting message: " + deleteMessageRequest);
        Message message = messageService.deleteMessage(deleteMessageRequest);
        this.sendMessageToUsers(
                getUsers(deleteMessageRequest),
                Converter.messageConvertToResponse(message)
        );
    }

    @Transactional
    public ChatResponse createChat(ChatCreateRequest createRequest) {
        List<ChatUser> users = chatUserService.getChatUsers(createRequest.getUsernames());

        Chat chat = new Chat();
        chat.setChatName(createRequest.getChatName());
        chat.setOwner(createRequest.getOwner());
        chat.setType(ChatType.valueOf(createRequest.getType()));
        chat.setUsers(users);
        chat = chatRepository.save(chat);

        for (ChatUser user : users) {
            chatUserService.addChatToUser(user, chat);
        }

        return Converter.chatConvertToResponse(chat);
    }

    @Transactional
    public void deleteChat(String fromId, Long chatId) {
        Chat chat = this.getChat(chatId);
        List<UserResponse> users = chat.getUsers().stream()
                .map(Converter::userConvertToResponse)
                .toList();

        if (chat.getType().equals(ChatType.PERSONAL)) {
            this.deleteChat(chat);
        }
        else {
           this.deleteGroup(chat, fromId);
        }

        this.sendMessageToUsers(
                users,
                MessageResponse.builder().type(String.valueOf(MessageType.SYSTEM)).build()
        );
    }

    @Transactional
    public void addUsersToChat(Long chatId, List<String> usernames) {
        List<ChatUser> newUsers = chatUserService.getChatUsers(usernames);

        Chat chat = this.getChat(chatId);
        List<ChatUser> users = chat.getUsers();
        users.addAll(newUsers);
        chat.setUsers(users);
        chat = chatRepository.save(chat);

        for (ChatUser user : newUsers) {
            chatUserService.addChatToUser(user, chat);
        }

        this.sendMessageToUsers(
                chat.getUsers().stream().map(Converter::userConvertToResponse).toList(),
                MessageResponse.builder().type(String.valueOf(MessageType.SYSTEM)).build()
        );
    }

    public ChatResponse getChatResponse(Long chatId) {
        return Converter.chatConvertToResponse(this.getChat(chatId));
    }

    private List<UserResponse> getUsers(MessageRequest message) {
        Chat chat = this.getChat(message.getChatId());
        return chat.getUsers()
                .stream()
                .map(Converter::userConvertToResponse)
                .toList();
    }

    private void sendMessageToUsers(List<UserResponse> users, MessageResponse message) {
        log.info("send message to: " + Arrays.toString(users.toArray()));
        for (UserResponse user : users) {
            messagingTemplate.convertAndSendToUser(
                    user.getUsername(), "/queue/messages", message);
        }
    }

    private Chat getChat(Long chatId) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(
                () -> new ApiException("chat not found", 500)
        );
        log.info("get chat: " + chat);
        return chat;
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

    private void deleteGroup(Chat chat, String fromId) {
        if (Objects.equals(chat.getOwner(), fromId)) {
            this.deleteChat(chat);
        }
        else {
            List<ChatUser> chatUsers = chat.getUsers();
            chatUsers.remove(chatUserService.findUser(fromId));
            chat.setUsers(chatUsers);
            chat = chatRepository.save(chat);
            chatUserService.deleteChat(fromId, chat);
            log.info("chat users after leaving user: " + chat.getUsers());
        }
    }

    private void deleteChat(Chat chat) {
        chatUserService.deleteUserFromChat(chat);
        messageService.deleteMessageByChat(chat);
        chatRepository.delete(chat);
    }
}
