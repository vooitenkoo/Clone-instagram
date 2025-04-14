package com.example.cloneInstragram.application.chat.service;

import com.example.cloneInstragram.application.chat.dto.ChatDTO;
import com.example.cloneInstragram.application.chat.dto.ChatUpdateDto;
import com.example.cloneInstragram.application.chat.dto.MessageDTO;
import com.example.cloneInstragram.application.chat.mapper.ChatMapper;
import com.example.cloneInstragram.application.chat.mapper.MessageMapper;
import com.example.cloneInstragram.application.user.service.NotificationService;
import com.example.cloneInstragram.domain.chat.model.Chat;
import com.example.cloneInstragram.domain.chat.model.Message;
import com.example.cloneInstragram.domain.chat.repository.ChatRepo;
import com.example.cloneInstragram.domain.chat.repository.MessageRepo;
import com.example.cloneInstragram.domain.user.model.User;
import com.example.cloneInstragram.domain.user.repository.UserRepo;
import com.example.cloneInstragram.infrastructure.web.exeption.ChatNotFoundException;
import com.example.cloneInstragram.infrastructure.web.exeption.UserNotFoundException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;@Service
public class ChatService {

    private final ChatRepo chatRepository;
    private final MessageRepo messageRepository;
    private final UserRepo userRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(ChatRepo chatRepository, MessageRepo messageRepository,
                       UserRepo userRepository, NotificationService notificationService,
                       SimpMessagingTemplate messagingTemplate) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public Chat createChat(Long userId1, Long userId2) {
        if (userId1.equals(userId2)) {
            throw new IllegalArgumentException("Cannot create a chat with the same user");
        }

        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId1));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId2));

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
                    Message lastMessage = lastMessageMap.get(chat.getId());
                    Long unreadCount = unreadMessagesCountMap.getOrDefault(chat.getId(), 0L);
                    return ChatMapper.toDto(chat, userId, lastMessage, unreadCount);
                })
                .toList();
    }

    @Transactional
    public List<User> getUsersInChat(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));
        return List.copyOf(chat.getUsers());
    }

    @Transactional
    public void markMessagesAsRead(Long chatId, Long userId) {
        int updated = messageRepository.markMessagesAsReadByChatAndSenderNot(chatId, userId);
        if (updated > 0) {

            Chat chat = chatRepository.findById(chatId)
                    .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));
            Long unreadCount = messageRepository.countByChatIdAndSenderNotAndReadFalse(chatId, userId);
            ChatUpdateDto updateDto = ChatMapper.toUpdateDto(chat, unreadCount);
            messagingTemplate.convertAndSend(
                    "/topic/chats/" + userId,
                    updateDto
            );
        }
    }

    @Transactional
    public Message saveMessage(Long chatId, User sender, String content) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found with id: " + chatId));

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
                ChatUpdateDto updateDto = ChatMapper.toUpdateDto(chat, unreadCount);
                messagingTemplate.convertAndSend(
                        "/topic/chats/" + user.getId(),
                        updateDto
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

    @Cacheable(value = "chatMessagesList", key = "{#senderId, #recipientId, #page, #size}")
    @Transactional(readOnly = true)
    public List<MessageDTO> findChatMessages(Long senderId, Long recipientId, int page, int size) {
        Chat chat = chatRepository.findPrivateChatBetweenUsers(senderId, recipientId)
                .orElseThrow(() -> new ChatNotFoundException("Chat not found between users " + senderId + " and " + recipientId));

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("sentAt").ascending());
        List<Message> messages = messageRepository.findByChatId(chat.getId(), pageRequest);

        return messages.stream()
                .map(MessageMapper::toMessageDto)
                .toList();
    }

    public Message findById(Long id) {
        return messageRepository.findById(id)
                .orElseThrow(() -> new ChatNotFoundException("Message not found with id: " + id));
    }
}