package com.example.cloneInstragram.application.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private int id;             // ID поста
    private String content;     // Текст поста
    private String imageUrl;    // Ссылка на изображение
    private int likeCount;      // Количество лайков
    private int commentCount;   // Количество комментариев
    private String username;    // Автор поста
}
