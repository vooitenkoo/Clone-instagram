package com.example.cloneInstragram.repos;

import com.example.cloneInstragram.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepo extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
   Optional<User> findById(Long id);
    List<User> findByUsernameContainingIgnoreCase(String username);
    List<User> findAllById(Iterable<Long> ids);

}