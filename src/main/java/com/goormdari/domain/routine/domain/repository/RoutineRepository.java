package com.goormdari.domain.routine.domain.repository;

import com.goormdari.domain.routine.domain.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RoutineRepository  extends JpaRepository<Routine, Long> {
    @Query("SELECT r FROM Routine r WHERE r.routineIndex = :routineIndex AND r.user.id = :userId")
    Routine findByRoutineIndexAndUserId(Long userId, Long routineIndex);

    @Query("SELECT r FROM Routine r WHERE r.user.id = :userId")
    List<Routine> findAllByUserId(Long userId);
}
