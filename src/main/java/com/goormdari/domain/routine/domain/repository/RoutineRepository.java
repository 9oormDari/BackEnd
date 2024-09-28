package com.goormdari.domain.routine.domain.repository;

import com.goormdari.domain.routine.domain.Routine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoutineRepository  extends JpaRepository<Routine, Long> {
    @Query("SELECT r FROM Routine r WHERE r.routineIndex = :routineIndex AND r.user.id = :userId")
    Routine findByRoutineIndexAndUserId(Long userId, Long routineIndex);

    @Query("SELECT r FROM Routine r WHERE r.user.id = :userId")
    List<Routine> findAllByUserId(Long userId);

    // 유저와 팀의 시작일~데드라인 사이에 해당하는 루틴 검색
    @Query("SELECT r FROM Routine r WHERE r.user.id = :userId AND DATE(r.createdAt) BETWEEN :startDate AND :endDate")
    List<Routine> findRoutinesByUserAndDateRange(@Param("userId") Long userId,
                                                 @Param("startDate") LocalDate startDate,
                                                 @Param("endDate") LocalDate endDate);
}
