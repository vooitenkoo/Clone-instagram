package com.example.cloneInstragram.services;

import com.example.cloneInstragram.dto.UserDTO;
import com.example.cloneInstragram.entity.Follow;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.repos.FollowRepo;
import com.example.cloneInstragram.repos.UserRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class FollowService {

    private final FollowRepo followRepo;
    private final UserRepo userRepo;
    private final UserService userService;

    public FollowService(FollowRepo followRepo, UserRepo userRepo, UserService userService) {
        this.followRepo = followRepo;
        this.userRepo = userRepo;
        this.userService = userService;
    }

    public void followUser(Long followerId, Long followingId) {
        User follower = userRepo.findById(followerId).orElseThrow(() -> new RuntimeException("Follower not found"));
        User following = userRepo.findById(followingId).orElseThrow(() -> new RuntimeException("User to follow not found"));

        // Проверяем, подписан ли уже
        Optional<Follow> existingFollow = followRepo.findByFollowerAndFollowing(follower, following);
        if (existingFollow.isPresent()) {
            throw new RuntimeException("Already following this user");
        }

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowing(following);
        followRepo.save(follow);
    }

    @Transactional
    public void unfollowUser(Long followerId, Long followingId) {
        User follower = userRepo.findById(followerId).orElseThrow(() -> new RuntimeException("Follower not found"));
        User following = userRepo.findById(followingId).orElseThrow(() -> new RuntimeException("User to unfollow not found"));

        followRepo.deleteByFollowerAndFollowing(follower, following);
    }

    // Получение списка подписчиков с подсчетом
    public List<UserDTO> getFollowers(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        int followersCount = followRepo.countByFollowing(user);
        return followRepo.findByFollowing(user)
                .stream()
                .map(Follow::getFollower)
                .map(follower -> userService.getUserDTO(follower, user))
                .collect(Collectors.toList());
    }

    // Получение списка тех, на кого подписан пользователь с подсчетом
    public List<UserDTO> getFollowing(Long userId) {
        User user = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        return followRepo.findByFollower(user)
                .stream()
                .map(Follow::getFollowing)
                .map(following -> userService.getUserDTO(following, user))
                .collect(Collectors.toList());
    }
}

