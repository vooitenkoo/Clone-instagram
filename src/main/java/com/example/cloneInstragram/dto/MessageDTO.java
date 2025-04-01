package com.example.cloneInstragram.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class MessageDTO {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("sender")
    private SimpleUserDTO sender;

    @JsonProperty("content")
    private String content;

    @JsonProperty("chatId")
    private Long chatId;

    public void setRead(boolean read) {
        this.read = read;
    }

    public boolean isRead() {
        return read;
    }

    public MessageDTO(Long id, SimpleUserDTO sender, String content, LocalDateTime sentAt, boolean read) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.sentAt = sentAt;
        this.read = read;
    }

    @JsonProperty("read")
    private boolean read;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime sentAt;


    public MessageDTO() {}

    public MessageDTO(Long id, SimpleUserDTO sender, String content, LocalDateTime sentAt) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.sentAt = sentAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SimpleUserDTO getSender() {
        return sender;
    }

    public void setSender(SimpleUserDTO sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    @Override
    public String toString() {
        return "MessageDTO{id=" + id + ", sender=" + sender + ", content='" + content + "', chatId=" + chatId + ", sentAt=" + sentAt + "}";
    }

}
