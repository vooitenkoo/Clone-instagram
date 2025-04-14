package com.example.cloneInstragram.application.user.mapper;


import com.example.cloneInstragram.application.user.dto.SimpleUserDTO;
import com.example.cloneInstragram.domain.user.model.User;

public class UserMapper {
    public static SimpleUserDTO toSimpleDto(User user) {
        return new SimpleUserDTO(user.getUsername());
    }
}