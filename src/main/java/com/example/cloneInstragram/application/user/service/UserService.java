package com.example.cloneInstragram.application.user.service;

import com.example.cloneInstragram.application.user.dto.SimpleUserDTO;
import com.example.cloneInstragram.application.user.dto.UserDTO;
import com.example.cloneInstragram.application.user.dto.UserRegisterDTO;
import com.example.cloneInstragram.application.user.mapper.UserMapper;
import com.example.cloneInstragram.domain.user.model.Role;
import com.example.cloneInstragram.domain.user.model.User;
import com.example.cloneInstragram.domain.user.repository.FollowRepo;
import com.example.cloneInstragram.domain.user.repository.UserRepo;
import org.hibernate.Hibernate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        User user = UserMapper.toUser(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(Role.USER));
        userRepo.save(user);
    }

    public List<User> searchUsersByUsername(String query) {
        return userRepo.findByUsernameContainingIgnoreCase(query);
    }

    @Transactional
    @Cacheable(value = "usersById", key = "#id")
    public Optional<User> findById(Long id) {
        Optional<User> userOptional = userRepo.findById(id);
        userOptional.ifPresent(user -> Hibernate.initialize(user.getRoles()));
        return userOptional;
    }

    @Transactional
    public UserDTO getUserDTO(User user, User currentUser) {
        boolean isFollowing = followRepo.existsByFollowerAndFollowing(currentUser, user);
        return UserMapper.toUserDto(user, isFollowing);
    }

    @Transactional
    @Cacheable(value = "users", key = "#username")
    public User findByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Hibernate.initialize(user.getRoles());
        return user;
    }

    @Transactional
    public SimpleUserDTO getSimpleUserDTO(User user) {
        return UserMapper.toSimpleDto(user);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#user.username"),
            @CacheEvict(value = "usersById", key = "#user.id")
    },
            put = {
                    @CachePut(value = "users", key = "#username"),
                    @CachePut(value = "usersById", key = "#user.id")
            })
    public User updateUser(User user, String username, String bio, String profilePicture, String password) {
        user.setUsername(username);
        user.setBio(bio);
        user.setProfilePicture(profilePicture);

        if (password != null && !password.isEmpty()) {
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepo.save(user);
        Hibernate.initialize(user.getRoles());
        return user;
    }

    public boolean passwordMatches(String rawPassword, User user) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}