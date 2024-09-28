package com.goormdari.domain.user.presentation;

import com.goormdari.domain.calendar.exception.InvalidTokenException;
<<<<<<< HEAD
import com.goormdari.domain.user.dto.response.UserInfoResponse;
import com.goormdari.domain.user.service.UserService;
import com.goormdari.domain.user.dto.response.FindCurrentStepResponse;
=======
import com.goormdari.domain.user.domain.dto.request.UpdateUserRequest;
import com.goormdari.domain.user.domain.dto.response.UserInfoResponse;
import com.goormdari.domain.user.domain.service.UserService;
import com.goormdari.domain.user.domain.dto.response.findCurrentStepResponse;
>>>>>>> main
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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Tag(name = "User", description = "User API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    @Operation(summary = "현재 루틴 수 조회", description = "사용자가 완수한 루틴의 개수")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공 ", content = {@Content(mediaType = "application/json", schema = @Schema(implementation =  FindCurrentStepResponse.class))}),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/current-step")
    public ResponseCustom<FindCurrentStepResponse> getCurrentStep(
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

    @Operation(summary = "현재 유저 정보 조회", description = "유저의 현재 데이터(nickname, username, email, profileImageUrl, goal, deadline, teamId) 조회")
    @GetMapping("/info")
    public UserInfoResponse getCurrentUserInfo(@Parameter(description = "Accesstoken을 입력해주세요.", required = true) @RequestHeader("Authorization") String token) {
        if (token == null) {
            throw new InvalidTokenException();
        }

        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        if (!jwtUtil.validateToken(jwt)) {
            throw new IllegalArgumentException("Invalid token");
        }
        Long userId = jwtUtil.extractId(jwt);

        return userService.getUserInfo(userId);
    }

    @Operation(summary = "현재 유저 프로필 업데이트", description = "유저의 nickname, username, password(지난 비밀번호 검증 과정 존재) email, profileImageUrl 업데이트 기능(null 값으로 전송 시, 업데이트 X)")
    @PostMapping
    public UserInfoResponse updateCurrentUserInfo(@Parameter(description = "Accesstoken을 입력해주세요.", required = true) @RequestHeader("Authorization") String token,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        if (token == null) {
            throw new InvalidTokenException();
        }

        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        if (!jwtUtil.validateToken(jwt)) {
            throw new IllegalArgumentException("Invalid token");
        }
        Long userId = jwtUtil.extractId(jwt);

        return userService.updateUserProfile(userId, updateUserRequest);
    }

}
