//package com.example.cloneInstragram.controller;
//
//import com.example.cloneInstragram.component.JwtResponse;
//import com.example.cloneInstragram.entity.User;
//import com.example.cloneInstragram.services.JwtService;
//import com.example.cloneInstragram.services.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Optional;
//@RestController
//@RequestMapping("/api")
//public class LoginController {
//
//    @Autowired
//    private UserService userService;
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Autowired
//    private JwtService jwtService;
//
//    @PostMapping("/login")
//    public ResponseEntity<?> loginUser(
//            @RequestParam String username,
//            @RequestParam String password
//    ) {
//        Optional<User> userOptional = userService.findByUsername(username);
//        if (userOptional.isPresent()) {
//            User user = userOptional.get();
//            if (!passwordEncoder.matches(password, user.getPassword())) {
//                return ResponseEntity.status(401).body("Invalid password");
//            }
//
//            // Генерация JWT токена
//            String token = jwtService.generateToken(username);
//            return ResponseEntity.ok(new JwtResponse(token));  // Возвращаем токен в формате JSON
//        } else {
//            return ResponseEntity.status(401).body("Username not found");
//        }
//    }
//}
//
//
