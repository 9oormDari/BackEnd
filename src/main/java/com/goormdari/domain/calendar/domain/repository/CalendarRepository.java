package com.goormdari.domain.calendar.domain.repository;

import com.goormdari.domain.calendar.domain.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
}
