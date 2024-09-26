package com.goormdari.domain.user.domain.repository;

import com.goormdari.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT t.joinCode FROM User u JOIN u.team t WHERE u.id = :userId")
    String findJoinCodeByUserId(Long userId);
}
