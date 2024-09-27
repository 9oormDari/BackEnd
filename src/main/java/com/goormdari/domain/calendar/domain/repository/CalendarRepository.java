package com.goormdari.domain.calendar.domain.repository;

import com.goormdari.domain.calendar.domain.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;

public interface CalendarRepository extends JpaRepository<Calendar, Long>, CalendarQueryDslRepository {

}
