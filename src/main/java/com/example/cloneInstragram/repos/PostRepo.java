package com.example.cloneInstragram.repos;

import com.example.cloneInstragram.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface PostRepo extends JpaRepository<Post, Long> {
    Page<Post> findByUserId(Integer userId, Pageable pageable);

    List<Post> findByUserIdIn(List<Long> followedUserIds);

    List<Post> findByIdIn(List<Long> recommendedPostIds);

    @Query("SELECT p FROM Post p ORDER BY SIZE(p.likes) DESC")
    List<Post> findTopTrendingPosts();
}
