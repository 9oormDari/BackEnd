package com.goormdari.domain.user.presentation;

import com.goormdari.domain.calendar.exception.InvalidTokenException;
import com.goormdari.domain.user.domain.service.UserService;
import com.goormdari.domain.user.domain.dto.response.findCurrentStepResponse;
import com.goormdari.global.config.security.jwt.JWTUtil;
import com.goormdari.global.payload.ErrorResponse;
import com.goormdari.global.payload.Message;
import com.goormdari.global.payload.ResponseCustom;
import com.goormdari.domain.user.domain.dto.response.findByTeamIdResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "User", description = "Routine API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    @Operation(summary = "현재 루틴 수 조회", description = "사용자가 완수한 루틴의 개수")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공 ", content = {@Content(mediaType = "application/json", schema = @Schema(implementation =  findCurrentStepResponse.class))}),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/current-step")
    public ResponseCustom<findCurrentStepResponse> getCurrentStep(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @RequestHeader("Authorization") String token
    ) {
        if (token == null) {
            throw new InvalidTokenException();
        }

        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        if (!jwtUtil.validateToken(jwt)) {
            throw new IllegalArgumentException("Invalid token");
        }
        Long userId = jwtUtil.extractId(jwt);
        return ResponseCustom.OK(userService.findCurrentStepById(userId));
    }

    @Operation(summary = "팀 참가 유저 명단 조회", description = "같은 팀에 존재하는 유저 명단")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공 ", content = {@Content(mediaType = "application/json", schema = @Schema(implementation =  findByTeamIdResponse.class))}),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/team-list")
    public ResponseCustom<List<findByTeamIdResponse>> getTeamMember(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @RequestHeader("Authorization") String token
    ) {
        if (token == null) {
            throw new InvalidTokenException();
        }

        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        if (!jwtUtil.validateToken(jwt)) {
            throw new IllegalArgumentException("Invalid token");
        }
        Long userId = jwtUtil.extractId(jwt);
        return ResponseCustom.OK(userService.findTeamByUserId(1L));
    }
}
