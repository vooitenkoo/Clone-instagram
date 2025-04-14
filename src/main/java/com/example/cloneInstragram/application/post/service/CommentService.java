package com.example.cloneInstragram.application.post.service;

import com.example.cloneInstragram.application.post.dto.CommentDto;
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

    public List<CommentDto> getCommentsByPost(Long postId ) {
        Post post = postService.getPostEntityById(postId);
        List<Comment> comments = commentRepo.findByPostIdOrderByCreatedAtDesc(post.getId());
        if(comments.isEmpty()) {
            return Collections.emptyList();
        }
        return comments.stream().map(this::toCommentDto).collect((Collectors.toList()));
    }

    private CommentDto toCommentDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getUser().getUsername(),
                comment.getCreatedAt()

        );
    }
    public void deleteComment(Long commentId) {
        if (!commentRepo.existsById(commentId)) {
            throw new RuntimeException("Comment not found");
        }
        commentRepo.deleteById(commentId);
    }

    public CommentDto createComment(User user, Long postId, String text) {
        Post post = postService.getPostEntityById(postId);

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setPost(post);
        comment.setText(text);
        commentRepo.save(comment);


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
        return toCommentDto(comment);
    }

}
