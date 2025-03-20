package com.example.cloneInstragram.repos;

import com.example.cloneInstragram.entity.Chat;
import com.example.cloneInstragram.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepo extends JpaRepository<Chat, Long> {
    List<Chat> findByUsersId(Long userId);
    @Query("SELECT c FROM Chat c JOIN c.users u1 JOIN c.users u2 " +
            "WHERE u1.id = :userId1 AND u2.id = :userId2 AND c.type = 'private'")
    Chat findPrivateChatBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    @Query("SELECT c FROM Chat c JOIN c.users u1 JOIN c.users u2 " +
            "WHERE u1.id = :senderId AND u2.id = :recipientId")
    Optional<Chat> findChatBetweenUsers(@Param("senderId") Long senderId, @Param("recipientId") Long recipientId);


}