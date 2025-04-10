package com.example.cloneInstragram.services;

import com.example.cloneInstragram.dto.ChatDTO;
import com.example.cloneInstragram.dto.ChatUpdateDto;
import com.example.cloneInstragram.dto.MessageDTO;
import com.example.cloneInstragram.dto.SimpleUserDTO;
import com.example.cloneInstragram.entity.Chat;
import com.example.cloneInstragram.entity.Message;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.repos.ChatRepo;
import com.example.cloneInstragram.repos.MessageRepo;
import com.example.cloneInstragram.repos.UserRepo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {
    private final ChatRepo chatRepository;
    private final MessageRepo messageRepository;
    private final UserRepo userRepo;

    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(ChatRepo chatRepository, MessageRepo messageRepository, UserRepo userRepo,
                       NotificationService notificationService, SimpMessagingTemplate messagingTemplate) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepo = userRepo;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public Chat createChat(Long userId1, Long userId2) {
        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("Cannot create a chat with the same user");
        }

        User user1 = userRepo.findById(userId1)
                .orElseThrow(() -> new IllegalArgumentException("User 1 not found"));
        User user2 = userRepo.findById(userId2)
                .orElseThrow(() -> new IllegalArgumentException("User 2 not found"));

        Optional<Chat> existingChat = chatRepository.findChatBetweenUsers(userId1, userId2);
        if (existingChat.isPresent()) {
            return existingChat.get();
        }

        Chat chat = new Chat();
        Set<User> users = new HashSet<>();
        users.add(user1);
        users.add(user2);
        chat.setUsers(users);
        chat.setType("PRIVATE");

        return chatRepository.save(chat);
    }
    @Cacheable(value = "userChats", key = "#userId")
    @Transactional(readOnly = true)
    public List<ChatDTO> getUserChats(Long userId) {
        List<Chat> chats = chatRepository.findByUsersIdWithUsers(userId);
        List<Long> chatIds = chats.stream().map(Chat::getId).toList();

        // Последние сообщения и непрочитанные одним запросом
        List<Message> lastMessages = messageRepository.findLastMessagesForChats(chatIds);
        Map<Long, Message> lastMessageMap = lastMessages.stream()
                .collect(Collectors.toMap(m -> m.getChat().getId(), m -> m));
        Map<Long, Long> unreadMessagesCountMap = messageRepository.countUnreadMessagesForChats(chatIds, userId);

        return chats.stream()
                .map(chat -> {
                    User otherUser = chat.getUsers().stream()
                            .filter(u -> !u.getId().equals(userId))
                            .findFirst()
                            .orElse(null);
                    Message lastMessage = lastMessageMap.get(chat.getId());
                    Long unreadCount = unreadMessagesCountMap.getOrDefault(chat.getId(), 0L);

                    return new ChatDTO(
                            chat.getId(),
                            otherUser != null ? otherUser.getUsername() : "Unknown",
                            otherUser != null ? otherUser.getProfilePicture() : null,
                            lastMessage != null ? lastMessage.getContent() : null,
                            lastMessage != null ? lastMessage.getSentAt() : null,
                            unreadCount
                    );
                })
                .toList();
    }
    @Transactional
    public List<User> getUsersInChat(Long chatId) {
        return chatRepository.findById(chatId)
                .map(Chat::getUsers)
                .map(List::copyOf)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
    }
    @Transactional
    public void markMessagesAsRead(Long chatId, Long userId) {
        int updated = messageRepository.markMessagesAsReadByChatAndSenderNot(chatId, userId);
        if (updated > 0) {
            Long unreadCount = messageRepository.countByChatIdAndSenderNotAndReadFalse(chatId, userId);
            messagingTemplate.convertAndSend(
                    "/topic/chats/" + userId,
                    new ChatUpdateDto(chatId, unreadCount)
            );
        }
    }
    @Transactional
    public Message saveMessage(Long chatId, User sender, String content) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setContent(content);
        message.setChat(chat);
        message.setSentAt(LocalDateTime.now());
        message.setRead(false); // Устанавливаем как непрочитанное

        Message savedMessage = messageRepository.save(message);

        for (User user : chat.getUsers()) {
            if (!user.getId().equals(sender.getId())) {
                notificationService.createNotification(
                        user,
                        sender,
                        sender.getUsername() + " sent you a message",
                        "MESSAGE",
                        chatId
                );
                // Отправляем обновление счётчика непрочитанных сообщений через WebSocket
                Long unreadCount = messageRepository.countByChatIdAndSenderNotAndReadFalse(chatId, user.getId());
                messagingTemplate.convertAndSend(
                        "/topic/chats/" + user.getId(),
                        new ChatUpdateDto(chatId, unreadCount)
                );
            }
        }
        return savedMessage;
    }

    public long countNewMessages(Long senderId, Long recipientId) {
        Optional<Chat> chat = chatRepository.findChatBetweenUsers(senderId, recipientId);
        if (chat.isEmpty()) {
            return 0L;
        }
        return messageRepository.countUnreadMessagesBySenderAndChat(chat.get().getId(), senderId);
    }
    @Transactional(readOnly = true)
    public List<MessageDTO> findChatMessages(Long senderId, Long recipientId, int page, int size) {
        Chat chat = chatRepository.findPrivateChatBetweenUsers(senderId, recipientId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found between users " + senderId + " and " + recipientId));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("sentAt").ascending());
        List<Message> messages = messageRepository.findByChatId(chat.getId(), pageRequest);

        // Подтягиваем пользователей чата один раз
        Map<Long, User> userMap = chat.getUsers().stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        return messages.stream()
                .map(message -> {
                    User sender = userMap.get(message.getSender().getId());
                    SimpleUserDTO senderDTO = new SimpleUserDTO(sender.getUsername());

                    MessageDTO messageDTO = new MessageDTO(
                            message.getId(),
                            senderDTO,
                            message.getContent(),
                            message.getSentAt(),
                            message.isRead()
                    );
                    messageDTO.setChatId(chat.getId());
                    return messageDTO;
                })
                .toList();
    }
    @Transactional
    public Chat getChatWithUsers(Long chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));
    }
    public Message findById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
    }
}