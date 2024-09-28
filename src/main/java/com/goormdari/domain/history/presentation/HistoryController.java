package com.goormdari.domain.history.presentation;

import com.goormdari.domain.calendar.exception.InvalidTokenException;
import com.goormdari.domain.history.application.HistoryService;
import com.goormdari.domain.history.domain.History;
import com.goormdari.domain.routine.domain.repository.RoutineRepository;
import com.goormdari.global.config.security.jwt.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/histories")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;
    private final RoutineRepository routineRepository;
    private final JWTUtil jwtUtil;

    @Operation(summary = "루틴 완수 기록 화면 조회", description = "루틴 완수 기록을 pageable 형태로 반홥합니다.")
    @GetMapping
    public ResponseEntity<Slice<History>> getHistoriesByUser(@Parameter(description = "Accesstoken을 입력해주세요.", required = true) @RequestHeader("Authorization") String token,
                                                             Pageable pageable) {
        if (token == null) {
            throw new InvalidTokenException();
        }

        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        if (!jwtUtil.validateToken(jwt)) {
            throw new IllegalArgumentException("Invalid token");
        }
        Long userId = jwtUtil.extractId(jwt);

        Slice<History> histories = historyService.getPagedHistoriesByUser(userId, pageable);
        return ResponseEntity.ok(histories);
    }

}
