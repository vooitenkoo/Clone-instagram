package com.example.cloneInstragram.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "follows")
@Data
public class Follow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User follower;  // Кто подписался

    @ManyToOne
    @JoinColumn(name = "following_id")
    private User following; // На кого подписались

    private LocalDateTime followedAt = LocalDateTime.now();
}
