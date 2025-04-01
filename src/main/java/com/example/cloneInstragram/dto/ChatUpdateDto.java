package com.example.cloneInstragram.dto;

public class ChatUpdateDto {
    private Long chatId;
    private Long unreadMessagesCount;

    public ChatUpdateDto(Long chatId, Long unreadMessagesCount) {
        this.chatId = chatId;
        this.unreadMessagesCount = unreadMessagesCount;
    }

    // Геттеры и сеттеры
    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getUnreadMessagesCount() {
        return unreadMessagesCount;
    }

    public void setUnreadMessagesCount(Long unreadMessagesCount) {
        this.unreadMessagesCount = unreadMessagesCount;
    }
}