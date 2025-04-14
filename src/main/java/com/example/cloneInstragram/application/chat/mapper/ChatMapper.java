package com.example.cloneInstragram.application.chat.mapper;

import com.example.cloneInstragram.application.chat.dto.ChatDTO;
import com.example.cloneInstragram.application.chat.dto.ChatUpdateDto;
import com.example.cloneInstragram.domain.chat.model.Chat;
import com.example.cloneInstragram.domain.chat.model.Message;
import com.example.cloneInstragram.domain.user.model.User;

public class ChatMapper {

    public static ChatDTO toDto(Chat chat, Long currentUserId, Message lastMessage, Long unreadMessagesCount) {
        User otherUser = chat.getUsers().stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Other user not found in chat"));

        return new ChatDTO(
                chat.getId(),
                otherUser.getUsername(),
                otherUser.getProfilePicture(),
                lastMessage != null ? lastMessage.getContent() : null,
                lastMessage != null ? lastMessage.getSentAt() : null,
                unreadMessagesCount
        );
    }


    public static ChatUpdateDto toUpdateDto(Chat chat, Long unreadMessagesCount) {
        return new ChatUpdateDto(
                chat.getId(),
                unreadMessagesCount
        );
    }

    public static void updateFromDto(Chat chat, ChatUpdateDto dto) {


    }
}