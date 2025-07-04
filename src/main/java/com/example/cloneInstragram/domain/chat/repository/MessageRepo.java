package com.example.cloneInstragram.domain.chat.repository;

import com.example.cloneInstragram.domain.chat.model.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {
    List<Message> findByChatId(Long chatId, Pageable pageable);

    @Query("SELECT m FROM Message m JOIN FETCH m.sender WHERE m.chat.id IN :chatIds AND m.sentAt = (SELECT MAX(m2.sentAt) FROM Message m2 WHERE m2.chat.id = m.chat.id)")
    List<Message> findLastMessagesForChats(@Param("chatIds") List<Long> chatIds);

    @Query("SELECT m.chat.id, COUNT(m) FROM Message m WHERE m.chat.id IN :chatIds AND " +
            "m.sender.id != :userId AND m.read = false GROUP BY m.chat.id")
    List<Object[]> countUnreadMessagesForChatsRaw(@Param("chatIds") List<Long> chatIds, @Param("userId") Long userId);

    default Map<Long, Long> countUnreadMessagesForChats(List<Long> chatIds, Long userId) {
        List<Object[]> results = countUnreadMessagesForChatsRaw(chatIds, userId);
        Map<Long, Long> resultMap = new HashMap<>();
        for (Object[] result : results) {
            Long chatId = (Long) result[0];
            Long count = (Long) result[1];
            resultMap.put(chatId, count);
        }
        return resultMap;
    }

    @Modifying
    @Query("UPDATE Message m SET m.read = true " +
            "WHERE m.chat.id = :chatId AND m.sender.id != :userId AND m.read = false")
    int markMessagesAsReadByChatAndSenderNot(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat.id = :chatId AND m.sender.id != :userId AND m.read = false")
    Long countByChatIdAndSenderNotAndReadFalse(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat.id = :chatId AND m.sender.id = :senderId AND m.read = false")
    Long countUnreadMessagesBySenderAndChat(@Param("chatId") Long chatId, @Param("senderId") Long senderId);
}
