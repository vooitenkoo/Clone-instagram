package com.example.cloneInstragram.controller;

import com.example.cloneInstragram.dto.ChatDTO;
import com.example.cloneInstragram.dto.MessageDTO;
import com.example.cloneInstragram.dto.UserDTO;
import com.example.cloneInstragram.entity.Chat;
import com.example.cloneInstragram.entity.Message;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.services.ChatService;
import com.example.cloneInstragram.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ChatWebSocketController {

    private final ChatService chatService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(ChatService chatService, UserService userService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.userService = userService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void processMessage(@Payload MessageDTO messageDTO) {
        if (messageDTO.getSender() == null || messageDTO.getSender().getId() == null ||
                messageDTO.getContent() == null || messageDTO.getChatId() == null) {
            throw new IllegalArgumentException("Sender, content, and chatId cannot be null");
        }

        System.out.println("📥 Received message: " + messageDTO);

        User sender = userService.findById(messageDTO.getSender().getId());

        List<User> chatUsers = chatService.getUsersInChat(messageDTO.getChatId());
        if (chatUsers.stream().noneMatch(u -> u.getId().equals(sender.getId()))) {
            throw new SecurityException("User is not a participant of this chat");
        }

        Message savedMessage = chatService.saveMessage(messageDTO.getChatId(), sender, messageDTO.getContent());

        MessageDTO responseDTO = new MessageDTO(
                savedMessage.getId(),
                userService.getSimpleUserDTO(sender),
                savedMessage.getContent(),
                savedMessage.getSentAt(),
                savedMessage.isRead()
        );
        responseDTO.setChatId(messageDTO.getChatId());

        messagingTemplate.convertAndSend("/topic/chat/" + messageDTO.getChatId(), responseDTO);
    }

    @GetMapping("/users/me")
    public ResponseEntity<UserDTO> getCurrentUser(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        UserDTO userDTO = userService.getUserDTO(currentUser, currentUser);
        userDTO.setId(currentUser.getId());
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/chats/create")
    public ResponseEntity<Chat> createChat(@RequestParam Long recipientId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Chat chat = chatService.createChat(currentUser.getId(), recipientId);
        return ResponseEntity.ok(chat);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        User user = userService.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        UserDTO userDTO = userService.getUserDTO(user, currentUser);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/chats")
    public ResponseEntity<List<ChatDTO>> getUserChats(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<ChatDTO> chats = chatService.getUserChats(currentUser.getId());
        return ResponseEntity.ok(chats);
    }

    @PostMapping("/messages/{chatId}/read")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable Long chatId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        chatService.markMessagesAsRead(chatId, currentUser.getId());
        return ResponseEntity.ok().build();

    }

    @GetMapping("/messages/{senderId}/{recipientId}/count")
    public ResponseEntity<Long> countNewMessages(@PathVariable Long senderId, @PathVariable Long recipientId) {
        return ResponseEntity.ok(chatService.countNewMessages(senderId, recipientId));
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<MessageDTO>> findChatMessages(
            @PathVariable Long senderId,
            @PathVariable Long recipientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        return ResponseEntity.ok(chatService.findChatMessages(senderId, recipientId, page, size));
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<?> findMessage(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.findById(id));
    }

    @GetMapping("/chats/{chatId}/users")
    public ResponseEntity<List<User>> getUsersInChat(@PathVariable Long chatId) {
        List<User> users = chatService.getUsersInChat(chatId);
        return ResponseEntity.ok(users);
    }
}