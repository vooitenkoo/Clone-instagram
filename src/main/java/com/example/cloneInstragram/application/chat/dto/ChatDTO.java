package com.example.cloneInstragram.application.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
public class ChatDTO {
    // Геттеры и сеттеры
    private Long id;
    private String otherUserUsername;
    private String otherUserProfilePicture;
    private String lastMessage;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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

}