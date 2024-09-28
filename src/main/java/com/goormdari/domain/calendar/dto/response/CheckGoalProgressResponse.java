package com.goormdari.domain.calendar.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record CheckGoalProgressResponse(
        String nickname,
        String profileUrl,
        String goal,
        int dDay,
        List<DayAchive> dayAchiveList
) {
    public record DayAchive(
            LocalDate date,
            int achieved
    ) {
    }

    @QueryProjection
    public CheckGoalProgressResponse(String nickname, String profileUrl, String goal, int dDay, List<DayAchive> dayAchiveList) {
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.goal = goal;
        this.dDay = dDay;
        this.dayAchiveList = dayAchiveList;
    }
}
