package com.example.cloneInstragram.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver; // Кто получает уведомление

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    private String type;
    private String message;
    private Long entityId;

    private boolean isRead = false;

    private LocalDateTime createdAt = LocalDateTime.now();
}
