package com.goormdari.domain.history.domain.application;

import com.goormdari.domain.history.domain.History;
import com.goormdari.domain.history.domain.dto.request.CreateHistoryRequest;
import com.goormdari.domain.history.domain.dto.response.UpdateHistoryResponse;
import com.goormdari.domain.history.domain.repository.HistoryRepository;
import com.goormdari.domain.team.domain.Team;
import com.goormdari.domain.team.domain.repository.TeamRepository;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
fn
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HistoryService {

    private final HistoryRepository historyRepository;
    private final TeamRepository teamRepository;
    private final RoutineRepository routineRepository;

    public Long createHistory(CreateHistoryRequest dto) {
        if (!hasAtLeastOneRoutine(dto.routine1(), dto.routine2(), dto.routine3(), dto.routine4())) {
            throw new IllegalArgumentException("루틴이 입력되지 않았습니다.");
        }

        Team team = teamRepository.findById(dto.teamId()).orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다."));

        Boolean isSuccess = updateHistorySuccessStatus(dto.routineIds());

        return historyRepository.save(History.builder()
                .goal(dto.goal())
                .routine1(dto.routine1())
                .routine2(dto.routine2())
                .routine3(dto.routine3())
                .routine4(dto.routine4())
                .isSuccess(isSuccess)
                .createAt(LocalDateTime.now())
//                .team(team)
                .build()).getId();
    }

    public Boolean hasAtLeastOneRoutine(String routine1, String routine2, String routine3, String routine4) {
        return (routine1 != null && routine1.trim().isEmpty()) ||
                (routine2 != null && routine2.trim().isEmpty()) ||
                (routine3 != null && routine3.trim().isEmpty()) ||
                (routine4 != null && routine4.trim().isEmpty());
    }

    public

    // 개별 루틴 성공 확인 메소드
    public Boolean isRoutineSuccessful(Long routineId) {
        Routine routine = routineRepository.findById(routineId).orElseTrow(() -> new IllegalArgumentException("루틴을 찾을 수 없습니다."));
        return routine.getRoutineImge() != null && !routine.getRoutineImge().isEmpty();
    }

    // 모든 루틴 성공 확인 메소드
    public Boolean updateHistorySuccessStatus(List<Long> routineIds) {
        for (Long routineId : routineIds) {
            if (!isRoutineSuccessful(routineId)) {
                return false;
            }
        }
        return true;
    }
}
