package com.example.cloneInstragram.repos;

import com.example.cloneInstragram.entity.Like;
import com.example.cloneInstragram.entity.Post;
import com.example.cloneInstragram.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepo extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndPostId(Long userId, Long postId);
    boolean existsByUserIdAndPostId(Long userId, Long postId);
}
