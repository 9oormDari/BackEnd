package com.goormdari.domain.calendar.domain.repository;

import com.goormdari.domain.calendar.dto.response.CheckGoalProgressResponse;

import java.time.YearMonth;

public interface CalendarQueryDslRepository {
    CheckGoalProgressResponse findByIdAndDate(Long userId, YearMonth date);
}
