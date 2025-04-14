package com.example.cloneInstragram.domain.user.repository;

import com.example.cloneInstragram.domain.user.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {

    List<Notification> findByReceiverId(Long userId);
}
