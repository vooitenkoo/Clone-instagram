package com.example.cloneInstragram.services;

import com.example.cloneInstragram.dto.MessageDTO;
import com.example.cloneInstragram.dto.UserDTO;
import com.example.cloneInstragram.entity.Chat;
import com.example.cloneInstragram.entity.Message;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.repos.ChatRepo;
import com.example.cloneInstragram.repos.MessageRepo;
import com.example.cloneInstragram.repos.UserRepo;
import com.example.cloneInstragram.repos.FollowRepo; // Импортируем репозиторий для Follow
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatRepo chatRepository;
    private final MessageRepo messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepo userRepository;
    private final UserService userService;

    public ChatService(ChatRepo chatRepository, MessageRepo messageRepository, SimpMessagingTemplate messagingTemplate, UserRepo userRepository, FollowRepo followRepository, UserService userService) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    // 📌 Создание или получение чата между пользователями
    public Chat createOrGetChat(User currentUser, Long otherUserId) {
        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Chat existingChat = chatRepository.findPrivateChatBetweenUsers(currentUser.getId(), otherUserId);
        if (existingChat != null) {
            return existingChat;
        }

        Chat chat = new Chat();
        chat.setUsers(Set.of(currentUser, otherUser));
        chat.setType("private");

        return chatRepository.save(chat);
    }

    public List<MessageDTO> getMessagesForChat(Long chatId, User currentUser) {

        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));


        User secondUser = chat.getUsers().stream()
                .filter(user -> !user.equals(currentUser))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Second user not found in chat"));

        List<Message> messages = messageRepository.findByChatId(chatId);

        return messages.stream().map(msg -> {
            User sender = msg.getSender();
            UserDTO senderDTO = userService.getUserDTO(secondUser, sender);

            return new MessageDTO(
                    msg.getId(),
                    senderDTO,  
                    msg.getContent(),
                    msg.getSentAt()
            );
        }).collect(Collectors.toList());
    }


    public Message sendMessage(Long chatId, User sender, String content) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
        Message message = new Message();
        message.setSender(sender);
        message.setChat(chat);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());
        messageRepository.save(message);


        return message;
    }

}
