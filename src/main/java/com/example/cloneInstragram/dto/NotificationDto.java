package com.example.cloneInstragram.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private Long id;
    private Long receiverId;
    private Long senderId;
    private String type;
    private String message;
    private Long entityId;
    private boolean isRead;
    private LocalDateTime createdAt;
}
