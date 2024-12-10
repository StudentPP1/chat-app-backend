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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public void sendMessage(SendMessageRequest request) {
        Chat chat = this.getChat(request.getChatId());
        Message message = this.addMessageToChat(chat, request);
        this.sendMessageToUsers(
                chat.getUsers().stream().map(Converter::userConvertToResponse).toList(),
                Converter.messageConvertToResponse(message)
        );
    }

    @Transactional
    public void updateMessage(UpdateMessageRequest request) {
        // update message & save
        Message message = messageService.updateMessage(request);
        // send to all users, except sender
        this.sendMessageToUsers(
                getUsers(request),
                Converter.messageConvertToResponse(message)
        );
    }

    @Transactional
    public void deleteMessage(DeleteMessageRequest request) {
        Message message = messageService.deleteMessage(request);
        this.sendMessageToUsers(
                getUsers(request),
                Converter.messageConvertToResponse(message)
        );
    }

    @Transactional
    public ChatResponse createChat(ChatCreateRequest request) {
        List<ChatUser> users = chatUserService.getChatUsers(request.getUsernames());

        Chat chat = new Chat();
        chat.setChatName(request.getChatName());
        chat.setOwner(request.getOwner());
        chat.setType(ChatType.valueOf(request.getType()));
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

    public void changeChatDetails(ChangeChatDetailsRequest request) {
        Chat chat = this.getChat(request.getChatId());
        chat.setChatName(request.getChatName());
        chat = chatRepository.save(chat);
        this.sendMessageToUsers(
                chat.getUsers().stream().map(Converter::userConvertToResponse).toList(),
                MessageResponse.builder().type(String.valueOf(MessageType.SYSTEM)).build()
        );
    }

    public void changeChatDetails(Long chatId, MultipartFile file) throws IOException {
        Chat chat = this.getChat(chatId);
        chat.setImg(file.getBytes());
        chat = chatRepository.save(chat);
        this.sendMessageToUsers(
                chat.getUsers().stream().map(Converter::userConvertToResponse).toList(),
                MessageResponse.builder().type(String.valueOf(MessageType.SYSTEM)).build()
        );
    }

    private List<UserResponse> getUsers(MessageRequest message) {
        Chat chat = this.getChat(message.getChatId());
        return chat.getUsers()
                .stream()
                .map(Converter::userConvertToResponse)
                .toList();
    }

    private void sendMessageToUsers(List<UserResponse> users, MessageResponse message) {
        for (UserResponse user : users) {
            messagingTemplate.convertAndSendToUser(
                    user.getUsername(), "/queue/messages", message);
        }
    }

    private Chat getChat(Long chatId) {
        return chatRepository.findById(chatId).orElseThrow(
                () -> new ApiException("chat not found", 500)
        );
    }

    private Message addMessageToChat(Chat chat, SendMessageRequest request) {
        Message message = Converter.requestConvertToMessage(request);
        message.setFrom(chatUserService.findUser(request.getFromId()));
        message.setChat(chat);
        message.setType(MessageType.SENT);
        message = messageService.saveMessage(message);

        List<Message> messages = chat.getMessages();
        messages.add(message);
        chat.setMessages(messages);
        chatRepository.save(chat);

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
        }
    }

    private void deleteChat(Chat chat) {
        chatUserService.deleteUserFromChat(chat);
        messageService.deleteMessageByChat(chat);
        chatRepository.delete(chat);
    }
}
