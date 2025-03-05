package com.example.cloneInstragram.controller;

import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.services.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<String> likePost(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        likeService.likePost(user, postId);
        return ResponseEntity.ok("Post liked");
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> unlikePost(@PathVariable Long postId, @AuthenticationPrincipal User user) {
        likeService.unlikePost(user, postId);
        return ResponseEntity.ok("Like removed");
    }
}
