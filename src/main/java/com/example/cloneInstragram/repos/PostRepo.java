package com.example.cloneInstragram.repos;

import com.example.cloneInstragram.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PostRepo extends JpaRepository<Post, Long> {
    Page<Post> findByUserId(Integer userId, Pageable pageable);
}
