package com.example.cloneInstragram.services;

import com.example.cloneInstragram.dto.UserDTO;
import com.example.cloneInstragram.dto.UserRegisterDTO;
import com.example.cloneInstragram.entity.Role;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.repos.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }


    public void register(UserRegisterDTO userDTO) {
        User user = new User();
        user.setName(userDTO.getName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRoles(Set.of(Role.USER)); // Назначаем роль USER
        userRepo.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }

    public UserDTO getUserDTO(User user) {
        return new UserDTO(user.getUsername(), user.getBio(), user.getProfilePicture());
    }

    public void updateUser(User user, String username, String bio, String profilePicture, String password) {
        user.setUsername(username);
        user.setBio(bio);
        user.setProfilePicture(profilePicture);

        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepo.save(user);
    }

    public boolean passwordMatches(String rawPassword, User user) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
