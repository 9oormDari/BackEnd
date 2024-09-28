package com.goormdari;

import com.goormdari.domain.history.application.HistoryService;
import com.goormdari.domain.history.domain.History;
import com.goormdari.domain.history.domain.dto.response.HistoryResponse;
import com.goormdari.domain.history.domain.repository.HistoryRepository;
import com.goormdari.domain.team.domain.Team;
import com.goormdari.domain.team.domain.repository.TeamRepository;
import com.goormdari.domain.user.domain.User;
import com.goormdari.domain.user.domain.repository.UserRepository;
import com.goormdari.domain.routine.domain.Routine;
import com.goormdari.domain.routine.domain.repository.RoutineRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HistoryServiceIntegrationTest {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private HistoryRepository historyRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoutineRepository routineRepository;

    private Team team;
    private User user;

    @BeforeEach
    void setUp() {
        // Create a team
        team = Team.builder()
                .goal("Integration Team Goal")
                .deadLine(LocalDate.of(2024, 12, 31))
                .routine1("Routine1")
                .routine2("Routine2")
                .routine3("Routine3")
                .routine4("Routine4")
                .joinCode("JOIN456")
                .build();
        teamRepository.save(team);

        // Create a user
        user = User.builder()
                .nickname("IntegrationUser")
                .username("integrationuser")
                .password("password123")
                .role("USER")
                .build();
        userRepository.save(user);
    }

    @Test
    void createHistory_ShouldPersistHistoryInDatabase() {
        Long userId = user.getId();
        Long teamId = team.getId();
        boolean isSuccess = true;

        // 히스토리 생성
        historyService.createHistory(userId, teamId, isSuccess);

        // 저장된 히스토리 확인
        List<History> histories = historyRepository.findAll();
        assertEquals(1, histories.size());

        History history = histories.get(0);
        assertEquals("Integration Team Goal", history.getGoal());
        assertEquals("Routine1", history.getRoutine1());
        assertEquals("Routine2", history.getRoutine2());
        assertEquals("Routine3", history.getRoutine3());
        assertEquals("Routine4", history.getRoutine4());
        assertEquals(userId, history.getUser().getId());
        assertTrue(history.getIsSuccess());
        assertNotNull(history.getCreateAt());
        assertEquals(0, history.getDDayPlus());
    }

    @Test
    void calculateGoalAchievement_ShouldPersistCorrectResult() {
        Long userId = user.getId();
        Long teamId = team.getId();

        // 75% 성공률 루틴 추가
        routineRepository.saveAll(List.of(
                Routine.builder().user(user).routineName("Routine1").routineImg("img1.png").build(),
                Routine.builder().user(user).routineName("Routine2").routineImg("img2.png").build(),
                Routine.builder().user(user).routineName("Routine3").routineImg("img3.png").build(),
                Routine.builder().user(user).routineName("Routine4").routineImg(null).build()
        ));

        boolean isAchieved = historyService.calculateGoalAchievement(userId, teamId);
        assertTrue(isAchieved); // 75% 성공률
    }

    @Test
    void calculateGoalAchievement_ShouldPersistIncorrectResult() {
        Long userId = user.getId();
        Long teamId = team.getId();

        // 50% 성공률 루틴 추가
        routineRepository.saveAll(List.of(
                Routine.builder().user(user).routineName("Routine1").routineImg("img1.png").build(),
                Routine.builder().user(user).routineName("Routine2").routineImg(null).build(),
                Routine.builder().user(user).routineName("Routine3").routineImg(null).build(),
                Routine.builder().user(user).routineName("Routine4").routineImg("img4.png").build()
        ));

        boolean isAchieved = historyService.calculateGoalAchievement(userId, teamId);
        assertFalse(isAchieved); // 50% 성공률
    }

    @Test
    void updateDDayPlus_ShouldPersistIncrementedValue() {
        // 히스토리 생성 및 저장
        History history1 = History.builder()
                .goal("Goal1")
                .routine1("Routine1")
                .routine2("Routine2")
                .routine3("Routine3")
                .routine4("Routine4")
                .user(user)
                .isSuccess(true)
                .dDayPlus(2)
                .build();

        History history2 = History.builder()
                .goal("Goal2")
                .routine1("Routine1")
                .routine2("Routine2")
                .routine3("Routine3")
                .routine4("Routine4")
                .user(user)
                .isSuccess(false)
                .dDayPlus(5)
                .build();

        historyRepository.saveAll(List.of(history1, history2));

        // D-Day 업데이트 실행
        historyService.updateDDayPlus();

        List<History> updatedHistories = historyRepository.findAll();
        assertEquals(2, updatedHistories.size());

        for (History history : updatedHistories) {
            if (history.getId().equals(history1.getId())) {
                assertEquals(3, history.getDDayPlus()); // dDayPlus 값이 1 증가
            } else if (history.getId().equals(history2.getId())) {
                assertEquals(6, history.getDDayPlus()); // dDayPlus 값이 1 증가
            }
        }
    }

    @Test
    void getHistoriesByUser_ShouldReturnListOfHistoryResponseDto() {
        Long userId = user.getId();
        Long teamId = team.getId();
        boolean isSuccess = true;

        // 히스토리 생성
        historyService.createHistory(userId, teamId, isSuccess);

        // 유저에 대한 히스토리 조회
        List<HistoryResponse> historyResponseDtos = historyService.getAllHistoriesByUser(userId);

        // 조회된 히스토리 확인
        assertNotNull(historyResponseDtos);
        assertEquals(1, historyResponseDtos.size());

        HistoryResponse dto = historyResponseDtos.get(0);
        assertEquals("Integration Team Goal", dto.getGoal());
        assertArrayEquals(new String[]{"Routine1", "Routine2", "Routine3", "Routine4"}, dto.getRoutineList());
        assertEquals("성공", dto.getResult());
        assertEquals(0, dto.getDDay()); // D-Day는 현재 시간 기준으로 0으로 예상됨
    }
}
