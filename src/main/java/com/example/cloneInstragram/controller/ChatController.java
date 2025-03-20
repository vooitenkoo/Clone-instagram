/*
package com.example.cloneInstragram.controller;

import com.example.cloneInstragram.dto.MessageDTO;
import com.example.cloneInstragram.entity.Chat;
import com.example.cloneInstragram.entity.Message;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.repos.FollowRepo;
import com.example.cloneInstragram.repos.UserRepo;
import com.example.cloneInstragram.services.ChatService;
import com.example.cloneInstragram.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
    @Autowired
    private ChatService chatService;

    @Autowired
    private UserService userService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<Chat> createChat(@AuthenticationPrincipal UserDetails user, @PathVariable Long userId) {
        User user1 = userService.findByUsername(user.getUsername());
        Chat chat = chatService.createOrGetChat(user1, userId);
        return ResponseEntity.status(201).body(chat);
    }

    @PostMapping("/{chatId}/message")
    public ResponseEntity<Message> sendMessage(@PathVariable Long chatId,
                                               @RequestParam String content,
                                               @AuthenticationPrincipal UserDetails user) {
        User sender = userService.findByUsername(user.getUsername());
        Message message = chatService.sendMessage(chatId, sender, content);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<MessageDTO>> getMessages(@PathVariable Long chatId, @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        List<MessageDTO> messages = chatService.getMessagesForChat(chatId, currentUser);
        return ResponseEntity.ok(messages);
    }
}
*/
