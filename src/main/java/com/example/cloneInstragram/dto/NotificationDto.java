package com.example.cloneInstragram.dto;

import java.time.LocalDateTime;

public record NotificationDto(
        Long id,
        String senderUsername,
        String message,
        String type,
        Long entityId,
        LocalDateTime createdAt,
        boolean read
) {}