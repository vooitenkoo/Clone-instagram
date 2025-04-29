package com.example.cloneInstragram.application.post.service;

import com.example.cloneInstragram.application.post.dto.CommentDto;
import com.example.cloneInstragram.application.post.mapper.CommentMapper;
import com.example.cloneInstragram.domain.post.model.Comment;
import com.example.cloneInstragram.domain.post.model.Post;
import com.example.cloneInstragram.domain.post.model.PostInteraction;
import com.example.cloneInstragram.domain.user.model.User;
import com.example.cloneInstragram.domain.post.repository.CommentRepo;
import com.example.cloneInstragram.domain.post.repository.PostInteractionRepo;
import com.example.cloneInstragram.application.user.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepo commentRepo;
    private final PostService postService;
    private final NotificationService notificationService;
    private final PostInteractionRepo postInteractionRepo;

    public CommentService(CommentRepo commentRepo, PostService postService, NotificationService notificationService, PostInteractionRepo postInteractionRepo) {
        this.commentRepo = commentRepo;
        this.postService = postService;
        this.notificationService = notificationService;
        this.postInteractionRepo = postInteractionRepo;
    }

    public List<Comment> getComments() {
        return commentRepo.findAll();
    }

    public List<CommentDto> getCommentsByPost(Long postId) {
        Post post = postService.getPostEntityById(postId);
        List<Comment> comments = commentRepo.findByPostIdOrderByCreatedAtDesc(post.getId());
        if (comments.isEmpty()) {
            return Collections.emptyList();
        }
        return comments.stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public void deleteComment(Long commentId) {
        if (!commentRepo.existsById(commentId)) {
            throw new RuntimeException("Comment not found");
        }
        commentRepo.deleteById(commentId);
    }

    public CommentDto createComment(User user, Long postId, String text) {
        Post post = postService.getPostEntityById(postId);

        // Используем маппер для создания Comment из DTO
        CommentDto commentDto = new CommentDto(0L, text, user.getUsername(), null);
        Comment comment = CommentMapper.toComment(commentDto);
        comment.setUser(user);
        comment.setPost(post);
        // createdAt уже устанавливается в сущности Comment по умолчанию

        Comment savedComment = commentRepo.save(comment);

        PostInteraction interaction = new PostInteraction();
        interaction.setUser(user);
        interaction.setPost(post);
        interaction.setType("COMMENT");
        postInteractionRepo.save(interaction);

        notificationService.createNotification(
                post.getUser(),  // Владелец поста (он получает уведомление)
                user,  // Кто оставил комментарий
                "COMMENT",
                user.getUsername() + " commented on your post",
                postId
        );

        return CommentMapper.toCommentDto(savedComment);
    }
}