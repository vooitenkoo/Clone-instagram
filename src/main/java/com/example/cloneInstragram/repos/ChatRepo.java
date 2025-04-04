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

    @Query("SELECT c FROM Chat c WHERE c.type = 'private' AND " +
            "EXISTS (SELECT 1 FROM c.users u1 WHERE u1.id = :senderId) AND " +
            "EXISTS (SELECT 1 FROM c.users u2 WHERE u2.id = :recipientId) AND " +
            "(SELECT COUNT(*) FROM c.users) = 2")
    Optional<Chat> findChatBetweenUsers(@Param("senderId") Long senderId, @Param("recipientId") Long recipientId);
    @Query("SELECT c FROM Chat c LEFT JOIN FETCH c.users u LEFT JOIN FETCH u.roles WHERE c.id = :id")
    Optional<Chat> findByIdWithUsers(@Param("id") Long id);

    @Query("SELECT c FROM Chat c WHERE c.type = 'private' " +
            "AND EXISTS (SELECT 1 FROM c.users u1 WHERE u1.id = :userId1) " +
            "AND EXISTS (SELECT 1 FROM c.users u2 WHERE u2.id = :userId2) " +
            "AND (SELECT COUNT(*) FROM c.users) = 2")
    Optional<Chat> findPrivateChatBetweenUsers(Long userId1, Long userId2);

    @Query("SELECT c FROM Chat c JOIN c.users u WHERE u.id = :userId")
    List<Chat> findByUsersId(@Param("userId") Long userId);
    List<Chat> findByUsersContaining(User user);
}