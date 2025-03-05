package com.example.cloneInstragram.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor

@Data
public class UserDTO {
    private String username;
    private String bio;
    private String profilePicture;
    private int followersCount;
    private int followingCount;
    private boolean isFollowing;

    public UserDTO(String username, String bio, String profilePicture, int followersCount, int followingCount, boolean isFollowing) {
        this.username = username;
        this.bio = bio;
        this.profilePicture = profilePicture;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
        this.isFollowing = isFollowing;
    }
}
