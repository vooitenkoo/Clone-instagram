package com.example.cloneInstragram.controller;

import com.example.cloneInstragram.dto.UserDTO;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.services.MinioService;
import com.example.cloneInstragram.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "http://localhost:3000")
public class ProfileController {

    private final UserService userService;
    private final MinioService minioService;

    public ProfileController(UserService userService, MinioService minioService) {
        this.userService = userService;
        this.minioService = minioService;
    }

    @GetMapping
    public ResponseEntity<UserDTO> getProfile(Principal principal) {
        User user = userService.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(userService.getUserDTO(user));
    }

    @PutMapping("/edit")
    public ResponseEntity<UserDTO> updateProfile(
            Principal principal,
            @RequestParam String username,
            @RequestParam String bio,
            @RequestParam(value = "profile_picture", required = false) MultipartFile profilePicture,
            @RequestParam String password) {

        User user = userService.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));

        String profilePicturePath = null;
        if (profilePicture != null && !profilePicture.isEmpty()) {
            profilePicturePath = minioService.uploadFile(profilePicture);
        }

        userService.updateUser(user, username, bio, profilePicturePath, password);
        return ResponseEntity.ok(new UserDTO(user.getUsername(), user.getBio(), user.getProfilePicture()));
    }
}
