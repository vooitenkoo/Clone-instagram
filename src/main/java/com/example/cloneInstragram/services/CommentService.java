package com.example.cloneInstragram.services;

import com.example.cloneInstragram.dto.CommentDto;
import com.example.cloneInstragram.dto.PostDto;
import com.example.cloneInstragram.entity.Comment;
import com.example.cloneInstragram.entity.Post;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.repos.CommentRepo;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepo commentRepo;
    private final PostService postService;

    public CommentService(CommentRepo commentRepo, PostService postService) {
        this.commentRepo = commentRepo;
        this.postService = postService;
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

        return toCommentDto(comment);
    }

}
