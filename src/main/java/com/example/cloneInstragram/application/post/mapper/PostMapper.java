package com.example.cloneInstragram.application.post.mapper;

import com.example.cloneInstragram.application.post.dto.PostDto;
import com.example.cloneInstragram.domain.post.model.Post;

public class PostMapper {

    public static PostDto toPostDto(Post post) {
        if (post == null) {
            return null;
        }
        // Обновляем likeCount перед маппингом
        post.updateLikeCount();
        return new PostDto(
                post.getId().intValue(), // Приводим Long к int, так как в PostDto id типа int
                post.getCaption(),    // caption в сущности соответствует content в DTO
                post.getImageUrl(),
                post.getLikeCount(),  // Берем likeCount, который обновляется методом updateLikeCount
                post.getComments() != null ? post.getComments().size() : 0,
                post.getUser() != null ? post.getUser().getUsername() : null
        );
    }

    public static Post toPost(PostDto postDto) {
        if (postDto == null) {
            return null;
        }
        Post post = new Post();
        post.setId((long) postDto.getId()); // Приводим int к Long, так как в Post id типа Long
        post.setCaption(postDto.getContent()); // content в DTO соответствует caption в сущности
        post.setImageUrl(postDto.getImageUrl());
        // Поля likes, comments и user не заполняются здесь, так как в PostDto есть только агрегированные данные.
        // Эти поля нужно будет установить в сервисе, если потребуется.
        return post;
    }
}