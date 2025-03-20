package com.example.cloneInstragram.repos;

import com.example.cloneInstragram.entity.Message;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findByChatId(Long chatId, Sort sentAt);

    long countBySenderIdAndChatId(Long senderId, Long recipientId);
}
