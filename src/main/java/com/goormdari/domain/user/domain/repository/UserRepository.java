package com.goormdari.domain.user.domain.repository;

import com.goormdari.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
