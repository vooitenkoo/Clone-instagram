package com.example.cloneInstragram.services;

import com.example.cloneInstragram.dto.UserDTO;
import com.example.cloneInstragram.entity.Role;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepo.findByUsername(username);

        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }


    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Set.of(Role.USER)); // Установите роли пользователя
        userRepo.save(user);
    }

    // Преобразование User в DTO
    public UserDTO getUserDTO(User user) {
        return new UserDTO(user.getUsername(), user.getBio(), user.getProfilePicture());
    }

    // Обновление данных пользователя
    public void updateUser(User user, String username, String bio, String profilePicture, String password) {
        user.setUsername(username);
        user.setBio(bio);
        user.setProfilePicture(profilePicture);

        if (!StringUtils.isEmpty(password)) {
            user.setPassword(passwordEncoder.encode(password));
        }

        userRepo.save(user);
    }

    // Загрузка пользователя по имени
    public Optional<User> findByUsername(String username) {
        return userRepo.findByUsername(username);
    }
}
