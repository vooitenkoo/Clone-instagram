package com.example.cloneInstragram.controller;

import com.example.cloneInstragram.dto.UserDTO;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.services.FollowService;
import com.example.cloneInstragram.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/follow")
public class FollowerController {

    private final FollowService followService;
    private final UserService userService;

    public FollowerController(FollowService followService, UserService userService) {
        this.followService = followService;
        this.userService = userService;
    }


    @PostMapping("/{followingId}")
    public ResponseEntity<String> followUser(@PathVariable Long followingId,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        User follower = userService.findByUsername(userDetails.getUsername());

        // Проверка: нельзя подписаться на себя
        if (follower.getId().equals(followingId)) {
            return ResponseEntity.badRequest().body("You cannot follow yourself");
        }

        followService.followUser(follower.getId(), followingId);
        return ResponseEntity.ok("Successfully followed user");
    }

    @DeleteMapping("/{followingId}")
    public ResponseEntity<String> unfollowUser(@PathVariable Long followingId,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        User follower = userService.findByUsername(userDetails.getUsername());

        if (follower.getId().equals(followingId)) {
            return ResponseEntity.badRequest().body("You cannot unfollow yourself");
        }

        followService.unfollowUser(follower.getId(), followingId);
        return ResponseEntity.ok("Successfully unfollowed user");
    }


    @GetMapping("/followers")
    public ResponseEntity<List<UserDTO>> getFollowers(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        // Получаем подписчиков в виде UserDTO
        List<UserDTO> followersDTO = followService.getFollowers(user.getId());

        return ResponseEntity.ok(followersDTO);
    }


    @GetMapping("/following")
    public ResponseEntity<List<UserDTO>> getFollowing(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());

        // Получаем подписки в виде UserDTO
        List<UserDTO> followingDTO = followService.getFollowing(user.getId());

        return ResponseEntity.ok(followingDTO);
    }
    // Получение подписчиков другого пользователя по ID
    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserDTO>> getFollowersByUserId(@PathVariable Long userId) {
        // Получаем пользователя по ID
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Получаем подписчиков этого пользователя
        List<UserDTO> followersDTO = followService.getFollowers(user.getId());

        return ResponseEntity.ok(followersDTO);
    }

    // Получение подписок другого пользователя по ID
    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserDTO>> getFollowingByUserId(@PathVariable Long userId) {
        // Получаем пользователя по ID
        User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Получаем подписки этого пользователя
        List<UserDTO> followingDTO = followService.getFollowing(user.getId());

        return ResponseEntity.ok(followingDTO);
    }

}
