package com.example.cloneInstragram.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDateTime;
import org.hibernate.annotations.Cache;

@Entity
@Table(name = "follows")
@Data
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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
