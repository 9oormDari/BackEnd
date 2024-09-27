package com.goormdari.domain.routine.domain.repository;

import com.goormdari.domain.routine.domain.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository  extends JpaRepository<Routine, Long> {
}
