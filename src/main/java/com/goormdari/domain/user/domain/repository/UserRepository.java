package com.goormdari.domain.user.domain.repository;

import com.goormdari.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    List<User> findAllByTeamId(Long teamId);

    @Query("SELECT t.joinCode FROM User u JOIN u.team t WHERE u.id = :userId")
    String findJoinCodeByUserId(Long userId);

    @Query("SELECT u FROM User u WHERE u.team.id = :teamId")
    List<User> findByTeamId(Long teamId);
}
