package com.example.cloneInstragram.controller;

import com.example.cloneInstragram.dto.MessageDTO;
import com.example.cloneInstragram.dto.UserDTO;
import com.example.cloneInstragram.entity.Message;
import com.example.cloneInstragram.entity.User;
import com.example.cloneInstragram.services.ChatService;
import com.example.cloneInstragram.services.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {
    private final ChatService chatService;
    private final UserService userService;

    public ChatWebSocketController(ChatService chatService, UserService userService) {
        this.chatService = chatService;
        this.userService = userService;
    }

    @MessageMapping("/chat/{chatId}") // Отправка сообщения
    @SendTo("/topic/chat/{chatId}")   // Сообщение отправляется всем подписчикам чата
    public MessageDTO sendMessage(@AuthenticationPrincipal UserDetails userDetails, Message message) {
        // Находим отправителя
        User sender = userService.findByUsername(userDetails.getUsername());

        // Отправляем сообщение через сервис
        Message savedMessage = chatService.sendMessage(message.getChat().getId(), sender, message.getContent());

        // Создаем DTO для отправителя (отправитель преобразуется в UserDTO)
        UserDTO senderDTO = userService.getUserDTO(sender, sender);
        System.out.println("Отправлено сообщение: " + savedMessage.getContent());
        // Создаём и возвращаем DTO для сообщения
        return new MessageDTO(
                savedMessage.getId(),
                senderDTO, // Используем UserDTO вместо User
                savedMessage.getContent(),
                savedMessage.getSentAt()
        );
    }

}
