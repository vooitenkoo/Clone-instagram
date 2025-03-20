package com.example.cloneInstragram.controller;

import com.example.cloneInstragram.dto.MessageDTO;
import com.example.cloneInstragram.entity.Message;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.services.ChatService;
import com.example.cloneInstragram.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
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
        if (messageDTO.getSender() == null || messageDTO.getContent() == null || messageDTO.getChatId() == null) {
            throw new IllegalArgumentException("Sender, content, and chatId cannot be null");
        }

        System.out.println("📥 Received message: " + messageDTO);

        User sender = userService.findByUsername(messageDTO.getSender().getUsername());
        if (sender == null) {
            throw new IllegalArgumentException("User not found for username: " + messageDTO.getSender().getUsername());
        }

        Message savedMessage = chatService.saveMessage(messageDTO.getChatId(), sender, messageDTO.getContent());

        // Отправляем сообщение в правильную очередь
        messagingTemplate.convertAndSend(
                "/topic/chat/" + messageDTO.getChatId(),
                new MessageDTO(
                        savedMessage.getId(),
                        userService.getUserDTO(sender, sender),
                        savedMessage.getContent(),
                        savedMessage.getSentAt()
                )
        );
    }

    @GetMapping("/messages/{senderId}/{recipientId}/count")
    public ResponseEntity<Long> countNewMessages(@PathVariable Long senderId, @PathVariable Long recipientId) {
        return ResponseEntity.ok(chatService.countNewMessages(senderId, recipientId));
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<?> findChatMessages(@PathVariable Long senderId, @PathVariable Long recipientId) {
        return ResponseEntity.ok(chatService.findChatMessages(senderId, recipientId));
    }

    @GetMapping("/messages/{id}")
    public ResponseEntity<?> findMessage(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.findById(id));
    }
}