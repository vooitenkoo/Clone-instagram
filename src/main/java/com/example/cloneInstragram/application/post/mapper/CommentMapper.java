package com.example.cloneInstragram.application.post.mapper;

import com.example.cloneInstragram.application.post.dto.CommentDto;
import com.example.cloneInstragram.domain.post.model.Comment;

public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getUser() != null ? comment.getUser().getUsername() : null,
                comment.getCreatedAt()
        );
    }

    public static Comment toComment(CommentDto commentDto) {
        if (commentDto == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setCreatedAt(commentDto.getCreatedAt());

        return comment;
    }
}