package com.example.cloneInstragram.services;

import com.example.cloneInstragram.dto.MessageDTO;
import com.example.cloneInstragram.dto.UserDTO;
import com.example.cloneInstragram.entity.Chat;
import com.example.cloneInstragram.entity.Message;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.repos.ChatRepo;
import com.example.cloneInstragram.repos.MessageRepo;
import com.example.cloneInstragram.repos.UserRepo;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatRepo chatRepository;
    private final MessageRepo messageRepository;
    private final UserRepo userRepo;
    private final UserService userService;

    public ChatService(ChatRepo chatRepository, MessageRepo messageRepository, UserRepo userRepo, UserService userService) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepo = userRepo;
        this.userService = userService;
    }

    // 📌 Найти пользователей в чате
    @Transactional
    public List<User> getUsersInChat(Long chatId) {
        return chatRepository.findById(chatId)
                .map(Chat::getUsers)  // Берём пользователей из найденного чата
                .map(List::copyOf)    // Преобразуем в List<User>
                .orElseThrow(() -> new RuntimeException("Chat not found"));
    }


    // 📌 Сохранить сообщение в чате
    public Message saveMessage(Long chatId, User sender, String content) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        message.setChat(chat);
        message.setSentAt(LocalDateTime.now());

        return messageRepository.save(message);
    }


    // 📌 Подсчет новых сообщений в чате
    public long countNewMessages(Long senderId, Long recipientId) {
        return messageRepository.countBySenderIdAndChatId(senderId, recipientId);
    }

    // 📌 Получить все сообщения чата
    public List<MessageDTO> findChatMessages(Long senderId, Long recipientId) {
        Optional<Chat> chat = chatRepository.findChatBetweenUsers(senderId, recipientId);

        if (chat.isEmpty()) {
            return List.of(); // Если чат не найден, возвращаем пустой список
        }

        List<Message> messages = messageRepository.findByChatId(
                chat.get().getId(),
                Sort.by(Sort.Direction.ASC, "sentAt")
        );

        return messages.stream().map(msg -> new MessageDTO(
                msg.getId(),
                userService.getUserDTO(msg.getSender(), msg.getSender()), // Создаём DTO пользователя
                msg.getContent(),
                msg.getSentAt()
        )).collect(Collectors.toList());
    }

    // 📌 Найти сообщение по ID
    public Message findById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }
}
