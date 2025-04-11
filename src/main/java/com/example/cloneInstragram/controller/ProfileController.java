package com.example.cloneInstragram.controller;

import com.example.cloneInstragram.dto.PostDto;
import com.example.cloneInstragram.dto.UserDTO;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.services.MinioService;
import com.example.cloneInstragram.services.PostService;
import com.example.cloneInstragram.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:3001")
public class ProfileController {

    private final UserService userService;
    private final MinioService minioService;
    private final PostService postService;

    public ProfileController(UserService userService, MinioService minioService, PostService postService) {
        this.userService = userService;
        this.minioService = minioService;
        this.postService = postService;
    }

    @PutMapping("/edit")
    public ResponseEntity<UserDTO> updateProfile(
            Principal principal,
            @RequestParam String username,
            @RequestParam String bio,
            @RequestParam(value = "profile_picture", required = false) MultipartFile profilePicture,
            @RequestParam String password) {

        User user = userService.findByUsername(principal.getName());

        String profilePicturePath = null;
        if (profilePicture != null && !profilePicture.isEmpty()) {
            profilePicturePath = minioService.uploadFile(profilePicture, "avatars");
        }

        userService.updateUser(user, username, bio, profilePicturePath, password);

        User updatedUser = userService.findByUsername(user.getUsername());


        int followersCount = updatedUser.getFollowers().size();
        int followingCount = updatedUser.getFollowing().size();
        boolean isFollowing = false;

        UserDTO userDTO = new UserDTO(
                updatedUser.getUsername(),
                updatedUser.getBio(),
                updatedUser.getProfilePicture(),
                followersCount,
                followingCount,
                isFollowing
        );

        return ResponseEntity.ok(userDTO);
    }


    @GetMapping({"/{username}", ""})
    public ResponseEntity<?> getProfileWithPosts(
            @PathVariable(required = false) String username,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (username == null) {
            username = userDetails.getUsername();
        }

        User currentUser = userService.findByUsername(userDetails.getUsername());
        User user = userService.findByUsername(username);

        UserDTO userDTO = userService.getUserDTO(user, currentUser);
        List<PostDto> posts = postService.getAllPosts(Math.toIntExact(user.getId()), offset, limit);

        Map<String, Object> response = new HashMap<>();
        response.put("profile", userDTO);
        response.put("posts", posts);

        return ResponseEntity.ok(response);
    }


}
