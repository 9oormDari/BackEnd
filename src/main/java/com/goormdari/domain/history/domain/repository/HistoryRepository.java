package com.goormdari.domain.history.domain.repository;

import com.goormdari.domain.history.domain.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
