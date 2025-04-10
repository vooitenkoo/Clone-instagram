package com.example.cloneInstragram.services;

import com.example.cloneInstragram.dto.SimpleUserDTO;
import com.example.cloneInstragram.dto.UserDTO;
import com.example.cloneInstragram.dto.UserRegisterDTO;
import com.example.cloneInstragram.entity.Role;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.repos.FollowRepo;
import com.example.cloneInstragram.repos.UserRepo;
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
        userOptional.ifPresent(user -> Hibernate.initialize(user.getRoles())); // Инициализируем roles
        return userOptional;
    }

    @Transactional
    public UserDTO getUserDTO(User user, User currentUser) {
        int followersCount = followRepo.countByFollowing(user);
        int followingCount = followRepo.countByFollower(user);
        boolean isFollowing = followRepo.existsByFollowerAndFollowing(currentUser, user);

        return new UserDTO(
                user.getUsername(),
                user.getBio(),
                user.getProfilePicture(),
                followersCount,
                followingCount,
                isFollowing
        );
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

    @Transactional
    @Cacheable(value = "users", key = "#username")
    public User findByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Hibernate.initialize(user.getRoles()); // Инициализируем roles
        return user;
    }

    @Transactional
    public SimpleUserDTO getSimpleUserDTO(User user) {
        return new SimpleUserDTO(user.getUsername());
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
        Hibernate.initialize(user.getRoles()); // Инициализируем roles перед возвратом
        return user;
    }

    public boolean passwordMatches(String rawPassword, User user) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}