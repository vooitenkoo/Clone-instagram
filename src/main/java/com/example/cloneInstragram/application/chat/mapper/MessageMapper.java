package com.example.cloneInstragram.application.chat.mapper;

import com.example.cloneInstragram.application.chat.dto.MessageDTO;
import com.example.cloneInstragram.application.user.dto.SimpleUserDTO;
import com.example.cloneInstragram.application.user.mapper.UserMapper;
import com.example.cloneInstragram.domain.chat.model.Message;

public class MessageMapper {

    public static MessageDTO toMessageDto(Message message) {
        SimpleUserDTO senderDto = UserMapper.toSimpleDto(message.getSender());
        MessageDTO messageDto = new MessageDTO(
                message.getId(),
                senderDto,
                message.getContent(),
                message.getSentAt(),
                message.isRead()
        );
        messageDto.setChatId(message.getChat().getId());
        return messageDto;
    }
}
