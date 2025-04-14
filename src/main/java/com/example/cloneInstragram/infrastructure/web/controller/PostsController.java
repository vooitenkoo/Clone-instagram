package com.example.cloneInstragram.infrastructure.web.controller;

import com.example.cloneInstragram.application.post.dto.PostDto;
import com.example.cloneInstragram.domain.user.model.User;
import com.example.cloneInstragram.application.post.service.PostService;
import com.example.cloneInstragram.application.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostsController {
    private final PostService postService;
    private final UserService userService;
    public PostsController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/createPosts")
    public ResponseEntity<PostDto> createPost(@AuthenticationPrincipal UserDetails userDetails,
                                              @RequestParam String content,
                                              @RequestParam MultipartFile image) {

        User user = userService.findByUsername(userDetails.getUsername());
        PostDto postDto = postService.createPost(user.getUsername(), content, image);


        return ResponseEntity.ok(postDto);
    }
    @GetMapping
    public ResponseEntity<List<PostDto>> getPosts(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit) {

        List<PostDto> posts = postService.getAllPosts(offset, limit);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/recommendation")
    public ResponseEntity<List<PostDto>> getRecommendations(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<PostDto> recommendedPosts = postService.getRecommendedPosts(user);
        return ResponseEntity.ok(recommendedPosts);
    }

}
