package com.example.cloneInstragram.services;

import com.example.cloneInstragram.dto.MessageDTO;
import com.example.cloneInstragram.dto.UserDTO;
import com.example.cloneInstragram.entity.Message;
import com.example.cloneInstragram.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;
    private final UserService userService;

    @Autowired
    public ChatMessageService(SimpMessagingTemplate messagingTemplate, ChatService chatService, UserService userService) {
        this.messagingTemplate = messagingTemplate;
        this.chatService = chatService;
        this.userService = userService;
    }

    // 📌 Отправка сообщения в конкретный чат
    public void sendMessageToUsers(Long chatId, Message savedMessage) {
        UserDTO senderDTO = userService.getUserDTO(savedMessage.getSender(), savedMessage.getSender());
        MessageDTO messageDTO = new MessageDTO(
                savedMessage.getId(),
                senderDTO,
                savedMessage.getContent(),
                savedMessage.getSentAt()
        );

        // Отправляем сообщение в чат с chatId
        messagingTemplate.convertAndSend(
                "/user/" + chatId + "/queue/messages",  // Подписка идет на chatId
                messageDTO
        );
        System.out.println("Message sent to " + chatId + " : " + savedMessage.getContent());
    }


}
