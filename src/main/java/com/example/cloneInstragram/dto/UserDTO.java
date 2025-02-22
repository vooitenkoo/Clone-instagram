package com.example.cloneInstragram.dto;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UserDTO {
    private String username;
    private String bio;
    private String profilePicture;

    // Конструкторы
    public UserDTO( String username, String bio, String profilePicture) {

        this.username = username;
        this.bio = bio;
        this.profilePicture = profilePicture;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
