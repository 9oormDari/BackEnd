package com.goormdari.domain.calendar.presentation;

import com.goormdari.domain.calendar.application.CalendarService;
import com.goormdari.domain.calendar.dto.response.CheckGoalProgressResponse;
import com.goormdari.domain.calendar.exception.InvalidTokenException;
import com.goormdari.global.config.security.jwt.JWTUtil;
import com.goormdari.global.payload.ErrorResponse;
import com.goormdari.global.payload.ResponseCustom;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@Tag(name = "Calendar", description = "Calendar API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/calendar")
public class CalendarController {

    // 마이페이지-목표점검 부분 api 입니다.
    private final CalendarService calendarService;

    private final JWTUtil jwtUtil;


    @Operation(summary = "목표 점검 화면 조회", description = "목표 점검 화면을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목표 점검 화면 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CheckGoalProgressResponse.class))}),
            @ApiResponse(responseCode = "400", description = "목표 점검 화면 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{date}")
    public ResponseCustom<CheckGoalProgressResponse> checkGoalProgress(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @RequestHeader("Authorization") String token,
            @Parameter(description = "조회할 날짜를 입력해주세요.(ex. 2024-08)", required = false) @PathVariable(required = false) YearMonth date
    ) {

        if (token == null) {
            throw new InvalidTokenException();
        }

        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        if (!jwtUtil.validateToken(jwt)) {
            throw new IllegalArgumentException("Invalid token");
        }
        String username = jwtUtil.extractUsername(jwt);
        return ResponseCustom.OK(calendarService.searchCheckGoalProgress(username, date));
    }

}
