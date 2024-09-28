package com.goormdari.domain.team.domain.repository;

import com.goormdari.domain.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByJoinCode(String joinCode);
    List<Team> findAllByDeadLineBefore(LocalDate today);
}
