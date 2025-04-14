package com.example.cloneInstragram.infrastructure.web.controller;

import com.example.cloneInstragram.application.user.dto.UserDTO;
import com.example.cloneInstragram.domain.user.model.User;
import com.example.cloneInstragram.application.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profile/search")
public class SearchController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> searchUsers(
            @RequestParam String query,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        User currentUser = userService.findByUsername(userDetails.getUsername());

        List<User> users = userService.searchUsersByUsername(query);
        List<UserDTO> userDTOs = users.stream()
                .map(user -> userService.getUserDTO(user, currentUser))
                .collect(Collectors.toList());

        return ResponseEntity.ok(userDTOs);
    }


}
