package com.example.cloneInstragram.services;

import com.example.cloneInstragram.dto.NotificationDto;
import com.example.cloneInstragram.entity.Notification;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.repos.NotificationRepo;
import com.example.cloneInstragram.repos.UserRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepo notificationRepository;
    private final UserRepo userRepository;

    public NotificationService(NotificationRepo notificationRepository, UserRepo userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public List<NotificationDto> getNotificationsForUser(Long userId) {
        return notificationRepository.findByReceiverId(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    public NotificationDto createNotification(Long receiverId, Long senderId, String type, String message, Long entityId) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        User sender = senderId != null ? userRepository.findById(senderId).orElse(null) : null;

        Notification notification = new Notification();
        notification.setReceiver(receiver);
        notification.setSender(sender);
        notification.setType(type);
        notification.setMessage(message);
        notification.setEntityId(entityId);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);

        return convertToDTO(notification);
    }

    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationDto convertToDTO(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setReceiverId(notification.getReceiver().getId());
        dto.setSenderId(notification.getSender() != null ? notification.getSender().getId() : null);
        dto.setType(notification.getType());
        dto.setMessage(notification.getMessage());
        dto.setEntityId(notification.getEntityId());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}
