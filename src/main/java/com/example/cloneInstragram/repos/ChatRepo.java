package com.example.cloneInstragram.repos;

import com.example.cloneInstragram.entity.Chat;
import com.example.cloneInstragram.entity.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(attributePaths = {"users", "users.roles"})
    Optional<Chat> findById(@Param("id") Long id);


    @Query("SELECT c FROM Chat c " +
            "JOIN FETCH c.users u " +
            "LEFT JOIN FETCH u.roles " +
            "WHERE :userId IN (SELECT u2.id FROM c.users u2) " +
            "ORDER BY (SELECT MAX(m.sentAt) FROM Message m WHERE m.chat.id = c.id) DESC NULLS LAST")
    List<Chat> findByUsersIdWithUsers(@Param("userId") Long userId);
    @Query("SELECT c FROM Chat c " +
            "JOIN FETCH c.users u " +
            "LEFT JOIN FETCH u.roles " +
            "WHERE c.type = 'private' AND " +
            "SIZE(c.users) = 2 AND " +
            ":senderId IN (SELECT u1.id FROM c.users u1) AND " +
            ":recipientId IN (SELECT u2.id FROM c.users u2)")
    Optional<Chat> findPrivateChatBetweenUsers(@Param("senderId") Long senderId, @Param("recipientId") Long recipientId);

}