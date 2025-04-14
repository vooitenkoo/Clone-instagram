package com.example.cloneInstragram.domain.user.repository;

import com.example.cloneInstragram.domain.user.model.Follow;
import com.example.cloneInstragram.domain.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepo extends JpaRepository<Follow, Long> {

    // Подсчитать количество подписчиков для пользователя
    int countByFollowing(User following);


    boolean existsByFollowerAndFollowing(User follower, User following);

    // Подсчитать количество подписок для пользователя
    int countByFollower(User follower);

    // Найти подписчиков конкретного пользователя
    List<Follow> findByFollowing(User following);

    // Найти, на кого подписан конкретный пользователь
    List<Follow> findByFollower(User follower);

    // Проверка, подписан ли один пользователь на другого
    Optional<Follow> findByFollowerAndFollowing(User follower, User following);

    // Удаление подписки
    void deleteByFollowerAndFollowing(User follower, User following);

    @Query("SELECT f.following.id FROM Follow f WHERE f.follower.id = :followingId")
    List<Long> findFollowedUserIdsByFollowingId(Long followingId);

}
