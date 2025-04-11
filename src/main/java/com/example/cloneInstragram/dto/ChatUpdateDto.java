package com.example.cloneInstragram.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatUpdateDto {
    // Геттеры и сеттеры
    private Long chatId;
    private Long unreadMessagesCount;

    public ChatUpdateDto(Long chatId, Long unreadMessagesCount) {
        this.chatId = chatId;
        this.unreadMessagesCount = unreadMessagesCount;
    }

}