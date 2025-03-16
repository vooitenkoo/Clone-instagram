package com.example.cloneInstragram.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private UserDTO sender;
    private String content;
    private LocalDateTime sentAt;

    public MessageDTO(Long id, UserDTO sender, String content, LocalDateTime sentAt) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.sentAt = sentAt;
    }
}