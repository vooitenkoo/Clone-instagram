package com.example.cloneInstragram.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class CommentDto {
    private Long id;
    private String text;
    private String username;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")// Вместо объекта User используем имя пользователя
    private LocalDateTime createdAt;

    public CommentDto(Long id, String text, String username, LocalDateTime createdAt) {
        this.id = id;
        this.text = text;
        this.username = username;
        this.createdAt = createdAt;
    }
}
