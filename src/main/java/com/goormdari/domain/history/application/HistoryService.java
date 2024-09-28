package com.goormdari.domain.history.application;

import com.goormdari.domain.history.domain.History;
import com.goormdari.domain.history.domain.dto.response.HistoryResponse;
import com.goormdari.domain.history.domain.repository.HistoryRepository;
import com.goormdari.domain.history.exception.ResourceNotFoundException;
import com.goormdari.domain.history.exception.UnValidUpdateHistories;
import com.goormdari.domain.routine.domain.Routine;
import com.goormdari.domain.routine.domain.repository.RoutineRepository;
import com.goormdari.domain.team.domain.Team;
import com.goormdari.domain.team.domain.repository.TeamRepository;
import com.goormdari.domain.user.domain.User;
import com.goormdari.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final TeamRepository teamRepository;
    private final RoutineRepository routineRepository;
    private final UserRepository userRepository;

    // 최초 기록 생성
    public void createHistory(Long userId, Long teamId) {
        // 팀 정보 가져오기
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team " + teamId + " not found"));

        // 팀의 시작일과 데드라인
        LocalDate startDate = team.getCreatedAt().toLocalDate();
        LocalDate deadline = team.getDeadLine();

        // 중복 확인: 해당 유저와 기간(시작일 ~ 데드라인)에 대한 기록이 이미 존재하는지 확인
        boolean historyExists = historyRepository.existsByUserIdAndDateRange(userId, startDate, deadline);

        if (historyExists) {
            // 이미 기록이 존재하면 더 이상 생성하지 않음
            return;
        }

        // 유저 정보 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User " + userId + " not found"));

        // 현재 날짜 확인
        LocalDate today = LocalDate.now();

        // 진행 상태
        String statusLabel;
        String dDayLabel;

        // 데드라인 확인
        if (today.isBefore(deadline)) {
            // deadline 이전
            statusLabel = "진행 중";
            long daysDifference = ChronoUnit.DAYS.between(today, deadline);
            dDayLabel = daysDifference > 0 ? "D-" + daysDifference
                    : daysDifference < 0 ? "D+" + Math.abs(daysDifference)
                    : "D-Day";
        } else if (today.isEqual(deadline)) {
            // 마감일 당일: "진행 중" 및 D-Day 표시
            statusLabel = "진행 중";
            dDayLabel = "D-Day";
        } else {
            // 데드라인 지난 후: "성공" 또는 "실패" 및 D-Day 표시
            boolean isSuccess = calculateGoalAchievement(userId, teamId);
            statusLabel = isSuccess ? "성공" : "실패";
            long daysDifference = ChronoUnit.DAYS.between(today, deadline);
            dDayLabel = "D+" + Math.abs(daysDifference);
        }

        // 히스토리 생성 및 저장
        History history = History.builder()
                .goal(team.getGoal())
                .routine1(team.getRoutine1())
                .routine2(team.getRoutine2())
                .routine3(team.getRoutine3())
                .routine4(team.getRoutine4())
                .user(user)  // 유저 정보 할당
                .dDayLabel(dDayLabel)
                .statusLabel(statusLabel)  // 70% 이상 성공 여부 반영
                .build();
        historyRepository.save(history);
    }

    // 마감일이 지난 히스토리를 갱신하여 상태와 D-Day를 업데이트
    @Scheduled(cron = "0 0 0 * * *")  // 매일 자정 실행
    public void updateHistoriesAfterDeadline() {

        updateHistoriesAfterDeadlineAsync();
    }

    // 비동기 메소드: 진행 중 상태의 모든 히스토리 갱신
    @Async
    @Transactional
    public CompletableFuture<Void> updateHistoriesAfterDeadlineAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                // 마감일이 지난 팀들의 모든 히스토리 조회
                List<History> expiredHistories = historyRepository.findAllExpiredHistories(LocalDate.now());

                if (expiredHistories.isEmpty()) {
                    throw new UnValidUpdateHistories("만료된 목표 기록들이 없습니다.");
                }

                int batchSize = 10;  // 배치 크기 설정

                for (int i = 0; i < expiredHistories.size(); i += batchSize) {
                    int end = Math.min(i + batchSize, expiredHistories.size());
                    List<History> batch = expiredHistories.subList(i, end);
                    processBatchExpiredHistories(batch);
                }
                log.info("updateExpiredHistoriesAsync completed at {}", LocalDateTime.now());
            } catch (UnValidUpdateHistories e) {
                throw e;
            } catch (Exception e) {
                throw new UnValidUpdateHistories("만료된 목표 기록들이 갱신되지 않았습니다.");
            }
            log.info("updateHistoriesAfterDeadlineAsync completed at {}", LocalDateTime.now());
        });
    }

    // In Progress 히스토리를 확인하고 성공 기준 충족 시 상태 업데이트
    @Scheduled(cron = "0 58 23 * * *") // 매일 23시 58분 실행
    public void scheduleUpdateInProgressHistories() {
        updateInProgressHistoriesAsync();
    }
    
    // 비동기 메소드: 진행 중 상태의 모든 히스토리 갱신
    @Async
    @Transactional
    public CompletableFuture<Void> updateInProgressHistoriesAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                List<History> inProgressHistories = historyRepository.findAllByStatusLabel("진행 중");

                if (inProgressHistories.isEmpty()) {
                    throw new UnValidUpdateHistories("'진행 중' 상태의 목표 기록들이 없습니다.");
                }

                int batchSize = 10;  // 배치 크기 설정

                for (int i = 0; i < inProgressHistories.size(); i += batchSize) {
                    int end = Math.min(i + batchSize, inProgressHistories.size());
                    List<History> batch = inProgressHistories.subList(i, end);
                    processBatchInProcess(batch);
                }
                log.info("updateInProgressHistoriesAsync completed at {}", LocalDateTime.now());
            } catch (UnValidUpdateHistories e) {
                throw e;
            } catch (Exception e) {
                throw new UnValidUpdateHistories("'진행 중' 상태의 목표 기록들이 갱신되지 않았습니다.");
            }
            log.info("updateInProgressHistoriesAsync completed at {}", LocalDateTime.now());
        });
    }

    private void processBatchInProcess(List<History> batch) {
        for (History history : batch) {
            boolean goalAchieved = calculateGoalAchievement(history.getUser().getId(), history.getUser().getTeam().getId());
            if (goalAchieved) {
                history.updateStatusLabel("성공");

                // D-Day 업데이트
                Team team = history.getUser().getTeam();
                LocalDate deadline = team.getDeadLine();
                String dDayLabel = calculateDDay(deadline);
                history.updateDDayLabel(dDayLabel);
            }
        }
        historyRepository.saveAll(batch);
    }

    private void processBatchExpiredHistories(List<History> batch) {
        for (History history : batch) {
            // 이미 성공/실패 상태인 경우 건너뜀
            if (history.getStatusLabel().equals("성공") || history.getStatusLabel().equals("실패")) {
                continue;
            }

            // 목표 달성 여부 계산
            boolean isSuccess = calculateGoalAchievement(history.getUser().getId(), history.getUser().getTeam().getId());

            // 상태 업데이트
            history.updateStatusLabel(isSuccess ? "성공" : "실패");

            // D-Day 업데이트
            String dDayLabel = calculateDDay(history.getUser().getTeam().getDeadLine());
            history.updateDDayLabel(dDayLabel);
        }
        historyRepository.saveAll(batch);
    }

    // 70% 이상 루틴 완수 시에 성공으로 계산
    public boolean calculateGoalAchievement(Long userId, Long teamId) {
        // 유저와 팀 정보 가져오기
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team " + teamId + " not found"));

        // 팀의 시작일과 데드라인 가져오기
        LocalDate startDate = team.getCreatedAt().toLocalDate();

        // 마감일 이전이면 현재까지 완료한 루틴 수 기준
        // 마감일 이후라면 마감일까지 완료한 루틴 수 기준
        LocalDate endDate = LocalDate.now().isBefore(team.getDeadLine()) ? LocalDate.now() : team.getDeadLine();

        // 목표 생성일부터 마감일까지의 총 루틴 수 (하루 4개 가정)
        long totalDays = ChronoUnit.DAYS.between(startDate, team.getDeadLine()) + 1; // 시작일 포함

        // 전체 루틴 개수 계산
        long totalRoutines = totalDays * 4;

        // 해당 기간 내에 유저가 수행한 루틴 목록 가져오기
        List<Routine> routines = routineRepository.findRoutinesByUserAndDateRange(userId, startDate, endDate);

        // 성공한 루틴 개수 계산 (이미지가 있는 경우 성공한 것으로 간주)
        long successfulRoutines = routines.stream()
                .filter(routine -> routine.getRoutineImg() != null && !routine.getRoutineImg().isEmpty())
                .count();

        // 성공률 계산 (70% 이상 성공 시 목표 달성)
        if (totalRoutines > 0) {
            double successRate = (double) successfulRoutines / totalRoutines;
            return successRate >= 0.7;
        }
        return false; // 루틴이 없는 경우 실패로 간주
    }

    public String calculateDDay(LocalDate deadline) {
        // D-Day 계산
        LocalDate today = LocalDate.now();
        long daysDifference = ChronoUnit.DAYS.between(today, deadline);
        String dDayLabel;
        if (daysDifference > 0) {
            dDayLabel = "D-" + daysDifference;
        } else if (daysDifference < 0) {
            dDayLabel = "D+" + Math.abs(daysDifference);
        } else {
            dDayLabel = "D-Day";
        }
        return dDayLabel;
    }

    // 특정 유저의 히스토리 리스트 조회
    public List<HistoryResponse> getAllHistoriesByUser(Long userId) {
        List<History> histories = historyRepository.findAllByUserId(userId);

        return histories.stream().map(history -> {
            String[] routines = {
                    history.getRoutine1(),
                    history.getRoutine2(),
                    history.getRoutine3(),
                    history.getRoutine4()
            };

            return new HistoryResponse(history.getDDayLabel(), history.getGoal(), routines, history.getStatusLabel());
        }).collect(Collectors.toList());
    }
}
