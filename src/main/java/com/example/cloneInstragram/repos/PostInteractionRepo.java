package com.example.cloneInstragram.repos;

import com.example.cloneInstragram.entity.PostInteraction;
import com.example.cloneInstragram.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostInteractionRepo extends JpaRepository<PostInteraction, Long> {

    @Query("SELECT p.post.id FROM PostInteraction p WHERE p.user.id = :userId")
    List<Long> findPostIdsLikedByUser(@Param("userId") Long userId);

    @Query("SELECT DISTINCT p.user FROM PostInteraction p WHERE p.post.id IN :likedPostIds AND p.user.id <> :userId")
    List<User> findUsersWhoLikedSamePosts(@Param("likedPostIds") List<Long> likedPostIds, @Param("userId") Long userId);

    @Query("SELECT DISTINCT p.post.id FROM PostInteraction p WHERE p.user IN :users")
    List<Long> findPostsLikedByUser(@Param("users") List<User> users);
}
