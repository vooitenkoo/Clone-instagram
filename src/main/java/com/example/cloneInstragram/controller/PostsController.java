package com.example.cloneInstragram.controller;

import com.example.cloneInstragram.dto.PostDto;
import com.example.cloneInstragram.dto.UserDTO;
import com.example.cloneInstragram.entity.Post;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.services.PostService;
import com.example.cloneInstragram.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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







}
