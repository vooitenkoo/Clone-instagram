package com.example.cloneInstragram.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CommentDto {
    private Long id;
    private String text;
    private String username; // Вместо объекта User используем имя пользователя
    private LocalDateTime createdAt;

    public CommentDto(Long id, String text, String username, LocalDateTime createdAt) {
        this.id = id;
        this.text = text;
        this.username = username;
        this.createdAt = createdAt;
    }
}
