package com.example.cloneInstragram.controller;

import com.example.cloneInstragram.dto.CommentDto;
import com.example.cloneInstragram.entity.Comment;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.services.CommentService;
import com.example.cloneInstragram.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/comments")
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;

    public CommentController(CommentService commentService, UserService userService) {
        this.commentService = commentService;
        this.userService = userService;
    }

    @PostMapping("/create/{postId}")
    public ResponseEntity<CommentDto> createComment(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long postId, @RequestParam String text) {
        User user = userService.findByUsername(userDetails.getUsername());
        CommentDto commentDto = commentService.createComment(user, postId, text);
        return  ResponseEntity.ok(commentDto);

    }
    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentDto>> getCommentsPost(@PathVariable Long postId) {
        List<CommentDto> commentDtoList = commentService.getCommentsByPost(postId);
        return  ResponseEntity.ok(commentDtoList);
    }
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        try {
            commentService.deleteComment(commentId);
            return ResponseEntity.noContent().build();  // Возвращает статус 204 No Content
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();  // Возвращает статус 404, если комментарий не найден
        }
    }
}
