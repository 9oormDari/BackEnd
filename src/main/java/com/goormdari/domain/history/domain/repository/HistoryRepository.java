package com.goormdari.domain.history.domain.repository;

import com.goormdari.domain.history.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {

    // 특정 유저와 기간(시작일~데드라인)에 대한 기록이 있는지 확인
    @Query("SELECT COUNT(h) > 0 FROM History h WHERE h.user.id = :userId AND DATE(h.createdAt) BETWEEN :startDate AND :endDate")
    boolean existsByUserIdAndDateRange(@Param("userId") Long userId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);

    List<History> findAllByUserId(Long userId);
}
