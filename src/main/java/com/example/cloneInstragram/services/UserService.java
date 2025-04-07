package com.example.cloneInstragram.services;


import com.example.cloneInstragram.dto.SimpleUserDTO;
import com.example.cloneInstragram.dto.UserDTO;
import com.example.cloneInstragram.dto.UserRegisterDTO;
import com.example.cloneInstragram.entity.Role;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.repos.FollowRepo;
import com.example.cloneInstragram.repos.UserRepo;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final FollowRepo followRepo;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, FollowRepo followRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.followRepo = followRepo;
    }


    public void register(UserRegisterDTO userDTO) {
        Optional<User> existingUser = userRepo.findByUsername(userDTO.getUsername());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Username is already taken");
        }
        User user = new User();
        user.setName(userDTO.getName());

        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setRoles(Set.of(Role.USER)); // Назначаем роль USER
        userRepo.save(user);
    }
    public List<User> searchUsersByUsername(String query) {
        return userRepo.findByUsernameContainingIgnoreCase(query);
    }
    public Optional<User> findById(Integer id) {
        return userRepo.findById(Long.valueOf(id));
    }

    public UserDTO getUserDTO(User user, User currentUser) {
        int followersCount = followRepo.countByFollowing(user);  // Получаем количество подписчиков
        int followingCount = followRepo.countByFollower(user);    // Получаем количество подписок
        boolean isFollowing = followRepo.existsByFollowerAndFollowing(currentUser, user);  // Проверка, подписан ли текущий пользователь

        return new UserDTO(
                user.getUsername(),
                user.getBio(),
                user.getProfilePicture(),
                followersCount,
                followingCount,
                isFollowing
        );
    }
    public User findById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User with ID " + id + " not found"));
    }
    public List<UserDTO> getAllUsersExceptCurrent(Long currentUserId) {
        return userRepo.findAll().stream()
                .filter(user -> !user.getId().equals(currentUserId))
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getProfilePicture(),
                        null,
                        null
                ))
                .collect(Collectors.toList());
    }
    public User findByUsername(String username) {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public SimpleUserDTO getSimpleUserDTO(User user) {
        return new SimpleUserDTO(user.getUsername());
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