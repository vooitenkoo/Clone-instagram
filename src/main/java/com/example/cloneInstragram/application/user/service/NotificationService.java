package com.example.cloneInstragram.application.user.service;

import com.example.cloneInstragram.application.user.dto.NotificationDto;
import com.example.cloneInstragram.application.user.mapper.NotificationMapper;
import com.example.cloneInstragram.domain.user.model.Notification;
import com.example.cloneInstragram.domain.user.model.User;
import com.example.cloneInstragram.domain.user.repository.NotificationRepo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepo notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepo notificationRepository, SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public void createNotification(User receiver, User sender, String message, String type, Long entityId) {
        Notification notification = new Notification();
        notification.setReceiver(receiver);
        notification.setSender(sender);
        notification.setMessage(message);
        notification.setType(type);
        notification.setEntityId(entityId);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);

        Notification savedNotification = notificationRepository.save(notification);

        NotificationDto notificationDto = NotificationMapper.toNotificationDto(savedNotification);
        messagingTemplate.convertAndSend("/topic/notifications/" + receiver.getId(), notificationDto);
    }

    public List<NotificationDto> getNotificationsForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByReceiverId(userId);
        return notifications.stream()
                .map(NotificationMapper::toNotificationDto)
                .toList();
    }

    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }
}