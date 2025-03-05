package com.example.cloneInstragram.controller;

import com.example.cloneInstragram.dto.UserDTO;
import com.example.cloneInstragram.dto.UserLoginDTO;
import com.example.cloneInstragram.dto.UserRegisterDTO;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.services.UserService;
import com.example.cloneInstragram.utils.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class RegistrationController {
    private final JwtUtil jwtUtil;

    private final UserService userService;
    @Autowired
    public RegistrationController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserRegisterDTO userDTO) {
        userService.register(userDTO);
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserLoginDTO userDTO) {
        Optional<User> userOptional = Optional.ofNullable(userService.findByUsername(userDTO.getUsername()));

        if (userOptional.isEmpty() || !userService.passwordMatches(userDTO.getPassword(), userOptional.get())) {
            return ResponseEntity.status(401).body("Invalid username or password");
        }

        String token = jwtUtil.generateToken(userDTO.getUsername());

        return ResponseEntity.ok(Map.of("token", token));
    }
}
