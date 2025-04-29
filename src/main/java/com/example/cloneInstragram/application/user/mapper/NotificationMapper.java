package com.example.cloneInstragram.application.user.mapper;

import com.example.cloneInstragram.application.user.dto.NotificationDto;
import com.example.cloneInstragram.domain.user.model.Notification;

public class NotificationMapper {

    public static NotificationDto toNotificationDto(Notification notification) {
        if (notification == null) {
            return null;
        }
        return new NotificationDto(
                notification.getId(),
                notification.getSender() != null ? notification.getSender().getUsername() : null,
                notification.getMessage(),
                notification.getType(),
                notification.getEntityId(),
                notification.getCreatedAt(),
                notification.isRead()
        );
    }

    public static Notification toNotification(NotificationDto notificationDto) {
        if (notificationDto == null) {
            return null;
        }
        Notification notification = new Notification();
        notification.setId(notificationDto.id());
        notification.setMessage(notificationDto.message());
        notification.setType(notificationDto.type());
        notification.setEntityId(notificationDto.entityId());
        notification.setCreatedAt(notificationDto.createdAt());
        notification.setRead(notificationDto.read());
        // Поля receiver и sender нужно установить в сервисе, так как в DTO есть только senderUsername
        return notification;
    }
}