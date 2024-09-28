package com.goormdari.domain.calendar.domain.repository;

import com.goormdari.domain.calendar.domain.Calendar;
import com.goormdari.domain.calendar.dto.response.CheckGoalProgressResponse;
import com.goormdari.domain.team.domain.QTeam;
import com.goormdari.domain.team.domain.Team;
import com.goormdari.domain.user.domain.QUser;
import com.goormdari.domain.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import static com.goormdari.domain.calendar.domain.QCalendar.calendar;
import static com.goormdari.domain.user.domain.QUser.user;

@RequiredArgsConstructor
@Repository
public class CalendarQueryDslRepositoryImpl implements CalendarQueryDslRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public CheckGoalProgressResponse findByIdAndDate(Long userId, YearMonth date) {

        LocalDate startDate = date.atDay(1);
        LocalDate endDate = date.atEndOfMonth();

        User userInfo = queryFactory
                .selectFrom(user)
                .where(user.id.eq(userId))
                .fetchOne();

        if (userInfo == null) {
            throw new EntityNotFoundException("User not found");
        }


        List<Calendar> calendarList = queryFactory
                .selectFrom(calendar)
                .where(
                        calendar.userId.eq(userId),
                        calendar.date.between(startDate, endDate)
                )
                .fetch();



        String goal = userInfo.getGoal();


        int dDay = calculateDDay(userInfo.getDeadLine());


        List<CheckGoalProgressResponse.DayAchive> dayAchiveList = calendarList.stream()
                .map(entry -> new CheckGoalProgressResponse.DayAchive(
                        entry.getDate(),
                        entry.getSuccess_count()
                ))
                .collect(Collectors.toList());


//        Double routineCompletionRate = calculateRoutineCompletionRate(userId, date);


        return new CheckGoalProgressResponse(
                userInfo.getNickname(),
                userInfo.getProfileUrl(),
                goal,
                dDay,
                dayAchiveList
        );
    }

    private Double calculateRoutineCompletionRate(Long userId, YearMonth date) {
        User user = queryFactory
                .selectFrom(QUser.user)
                .where(QUser.user.id.eq(userId))
                .fetchOne();
        Long teamId = user.getTeam().getId();

        Team team = queryFactory
                .selectFrom(QTeam.team)
                .where(QTeam.team.id.eq(teamId))
                .fetchOne();

        LocalDateTime createdAt = team.getCreatedAt();

        LocalDate deadLine = team.getDeadLine();
        LocalDate createdAtDate = createdAt.toLocalDate();

        // 일 수 차이 계산
        Long daysBetween = ChronoUnit.DAYS.between(createdAtDate, deadLine);

        // 루틴 4개 수행한 날짜 계산
        Long successCount = queryFactory
                .select(calendar.count())
                .from(calendar)
                .where(calendar.userId.eq(userId),
                        calendar.success_count.eq(4)
                )
                .fetchFirst();

        Double rate = ((double) (successCount) / (double) (daysBetween)) * 100;

        double roundedRate = Math.round(rate * 10) / 10.0;

        return roundedRate;
    }

    private int calculateDDay(LocalDate deadLine) {
        LocalDate now = LocalDate.now();
        return (int) ChronoUnit.DAYS.between(now, deadLine);
    }
}
