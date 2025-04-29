package com.example.cloneInstragram.application.user.mapper;

import com.example.cloneInstragram.application.user.dto.SimpleUserDTO;
import com.example.cloneInstragram.application.user.dto.UserDTO;
import com.example.cloneInstragram.application.user.dto.UserLoginDTO;
import com.example.cloneInstragram.application.user.dto.UserRegisterDTO;
import com.example.cloneInstragram.domain.user.model.User;
import com.example.cloneInstragram.domain.user.repository.FollowRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private static FollowRepo followRepo;

    @Autowired
    public UserMapper(FollowRepo followRepo) {
        UserMapper.followRepo = followRepo;
    }

    public static SimpleUserDTO toSimpleDto(User user) {
        return new SimpleUserDTO(user.getUsername());
    }

    public static UserDTO toUserDto(User user, boolean isFollowing) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
                user.getUsername(),
                user.getBio(),
                user.getProfilePicture(),
                followRepo.countByFollowing(user), // Подсчет через репозиторий
                followRepo.countByFollower(user),  // Подсчет через репозиторий
                isFollowing
        );
    }

    public static User toUser(UserDTO userDto) {
        if (userDto == null) {
            return null;
        }
        User user = new User();
        user.setId(userDto.getId());
        user.setUsername(userDto.getUsername());
        user.setBio(userDto.getBio());
        user.setProfilePicture(userDto.getProfilePicture());
        return user;
    }

    public static User toUser(UserRegisterDTO userRegisterDto) {
        if (userRegisterDto == null) {
            return null;
        }
        User user = new User();
        user.setName(userRegisterDto.getName());
        user.setUsername(userRegisterDto.getUsername());
        user.setPassword(userRegisterDto.getPassword());
        user.setEmail(userRegisterDto.getEmail());
        return user;
    }

    public static UserRegisterDTO toUserRegisterDto(User user) {
        if (user == null) {
            return null;
        }
        UserRegisterDTO userRegisterDto = new UserRegisterDTO();
        userRegisterDto.setName(user.getName());
        userRegisterDto.setUsername(user.getUsername());
        userRegisterDto.setPassword(user.getPassword());
        userRegisterDto.setEmail(user.getEmail());
        return userRegisterDto;
    }

    public static User toUser(UserLoginDTO userLoginDto) {
        if (userLoginDto == null) {
            return null;
        }
        User user = new User();
        user.setUsername(userLoginDto.getUsername());
        user.setPassword(userLoginDto.getPassword());
        return user;
    }

    public static UserLoginDTO toUserLoginDto(User user) {
        if (user == null) {
            return null;
        }
        UserLoginDTO userLoginDto = new UserLoginDTO();
        userLoginDto.setUsername(user.getUsername());
        userLoginDto.setPassword(user.getPassword());
        return userLoginDto;
    }
}