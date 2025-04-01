package com.example.cloneInstragram.dto;

import java.time.LocalDateTime;

public class ChatDTO {
    private Long id;
    private String otherUserUsername;
    private String otherUserProfilePicture;
    private String lastMessage;
    private LocalDateTime lastMessageSentAt;
    private Long unreadMessagesCount;

    public ChatDTO(Long id, String otherUserUsername, String otherUserProfilePicture, String lastMessage, LocalDateTime lastMessageSentAt, Long unreadMessagesCount) {
        this.id = id;
        this.otherUserUsername = otherUserUsername;
        this.otherUserProfilePicture = otherUserProfilePicture;
        this.lastMessage = lastMessage;
        this.lastMessageSentAt = lastMessageSentAt;
        this.unreadMessagesCount = unreadMessagesCount;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOtherUserUsername() {
        return otherUserUsername;
    }

    public void setOtherUserUsername(String otherUserUsername) {
        this.otherUserUsername = otherUserUsername;
    }

    public String getOtherUserProfilePicture() {
        return otherUserProfilePicture;
    }

    public void setOtherUserProfilePicture(String otherUserProfilePicture) {
        this.otherUserProfilePicture = otherUserProfilePicture;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastMessageSentAt() {
        return lastMessageSentAt;
    }

    public void setLastMessageSentAt(LocalDateTime lastMessageSentAt) {
        this.lastMessageSentAt = lastMessageSentAt;
    }

    public Long getUnreadMessagesCount() {
        return unreadMessagesCount;
    }

    public void setUnreadMessagesCount(Long unreadMessagesCount) {
        this.unreadMessagesCount = unreadMessagesCount;
    }
}