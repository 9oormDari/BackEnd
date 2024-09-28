package com.goormdari.domain.history.application;

import com.goormdari.domain.history.domain.History;
import com.goormdari.domain.history.domain.repository.HistoryRepository;
import com.goormdari.domain.history.exception.ResourceNotFoundException;
import com.goormdari.domain.routine.domain.Routine;
import com.goormdari.domain.routine.domain.repository.RoutineRepository;
import com.goormdari.domain.team.domain.Team;
import com.goormdari.domain.team.domain.repository.TeamRepository;
import com.goormdari.domain.user.domain.User;
import com.goormdari.domain.user.domain.repository.UserRepository;
import com.goormdari.global.config.security.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final TeamRepository teamRepository;
    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;

    // 최초 기록 생성
    public void createHistory(Long userId, Long teamId, boolean isSuccess) {
        // 팀 정보 가져오기
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team " + teamId + " not found"));

        // 팀의 시작일과 데드라인
        LocalDate startDate = team.getCreatedAt().toLocalDate();
        LocalDate endDate = team.getDeadLine();

        // 중복 확인: 해당 유저와 기간(시작일 ~ 데드라인)에 대한 기록이 이미 존재하는지 확인
        boolean historyExists = historyRepository.existsByUserIdAndDateRange(userId, startDate, endDate);

        if (historyExists) {
            // 이미 기록이 존재하면 더 이상 생성하지 않음
            return;
        }

        // 유저 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " not found"));

        // 히스토리 생성 및 저장
        History history = History.builder()
                .goal(team.getGoal())
                .routine1(team.getRoutine1())
                .routine2(team.getRoutine2())
                .routine3(team.getRoutine3())
                .routine4(team.getRoutine4())
                .user(user)  // 유저 정보 할당
                .isSuccess(isSuccess)  // 70% 이상 성공 여부 반영
                .build();
        historyRepository.save(history);
    }


    @Scheduled(cron = "0 0 0 * * *")  // 매일 자정 실행
    public void checkTeamGoalsAndCreateHistory() {
        LocalDate today = LocalDate.now();
        List<Team> expiredTeams = teamRepository.findAllByDeadLineBefore(today);

        for (Team team : expiredTeams) {
            List<User> teamMembers = userRepository.findAllByTeamId(team.getId());

            for (User user : teamMembers) {
                // 목표 달성 여부 계산
                boolean isGoalAchieved = calculateGoalAchievement(user.getId(), team.getId());

                // 중복 기록 생성 방지 처리된 히스토리 생성
                createHistory(user.getId(), team.getId(), isGoalAchieved);
            }
        }
    }

    // D-Day 자정마다 초기화
    // 매일 자정에 실행 (cron 표현식: "0 0 0 * * *")
    @Scheduled(cron = "0 0 0 * * *")
    public void updateDDayPlus() {
        // 모든 히스토리를 조회
        List<History> histories = historyRepository.findAll();

        for (History history : histories) {
            // dDayPlus 값을 1씩 증가
            history.incrementDDayPlus();
        }
        // 업데이트된 히스토리 저장
        historyRepository.saveAll(histories);
    }

    // 70% 이상 루틴 완수 시에 성공으로 계산
    public boolean calculateGoalAchievement(Long userId, Long teamId) {
        // 유저와 팀 정보 가져오기
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team " + teamId + " not found"));

        // 팀의 시작일과 데드라인 가져오기
        LocalDate startDate = team.getCreatedAt().toLocalDate();
        LocalDate endDate = team.getDeadLine();

        // 해당 기간 내에 유저가 수행한 루틴 목록 가져오기
        List<Routine> routines = routineRepository.findRoutinesByUserAndDateRange(userId, startDate, endDate);

        // 성공한 루틴 개수 계산 (이미지가 있는 경우 성공한 것으로 간주)
        long successfulRoutines = routines.stream()
                .filter(routine -> routine.getRoutineImg() != null && !routine.getRoutineImg().isEmpty())
                .count();

        // 전체 루틴 개수 계산
        long totalRoutines = routines.size();

        // 성공률 계산 (70% 이상 성공 시 목표 달성)
        if (totalRoutines > 0) {
            double successRate = (double) successfulRoutines / totalRoutines;
            return successRate >= 0.7;
        }
        return false; // 루틴이 없는 경우 실패로 간주
    }

    // 특정 유저의 히스토리 페이징 처리
    public Slice<History> getPagedHistoriesByUser(Long userId, Pageable pageable) {
        return historyRepository.findAllByUserId(userId, pageable);
    }
}
