package com.goormdari.domain.team.presentation;

import com.goormdari.domain.calendar.exception.InvalidTokenException;
import com.goormdari.domain.team.application.TeamService;
import com.goormdari.domain.team.dto.request.CreateTeamRequest;
import com.goormdari.domain.team.dto.response.CreateTeamResponse;
import com.goormdari.domain.team.dto.response.findAllRoutineByUserIdResponse;
import com.goormdari.domain.user.domain.dto.response.findByTeamIdResponse;
import com.goormdari.global.config.security.jwt.JWTUtil;
import com.goormdari.global.payload.ErrorResponse;
import com.goormdari.global.payload.Message;
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

import java.util.List;

@Tag(name = "Team", description = "Team API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

    private final JWTUtil jwtUtil;

    @Operation(summary = "팀(방) 생성", description = "팀(방)을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팀(방) 생성 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CreateTeamResponse.class))}),
            @ApiResponse(responseCode = "400", description = "팀(방) 생성 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping
    public ResponseCustom<CreateTeamResponse> createTeam(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @RequestHeader("Authorization") String token,
            @Parameter(description = "Schemas의 CreateTeamRequest를 참고해주세요.", required = true) @Valid @RequestBody CreateTeamRequest createTeamRequest
    ) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        if (!jwtUtil.validateToken(jwt)) {
            throw new IllegalArgumentException("Invalid token");
        }
        String username = jwtUtil.extractUsername(jwt);
        return ResponseCustom.OK(teamService.createNewTeam(username, createTeamRequest));
    }

    @Operation(summary = "팀(방) 참여코드 전달", description = "팀(방) 참여코드를 이메일로 전달합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팀(방) 참여코드 전달 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "팀(방) 참여코드 전달 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/email/{guestId}")
    public ResponseCustom<Message> sendJoinCode(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @RequestHeader("Authorization") String token,
            @Parameter(description = "초대할 유저의 id를 입력해주세요.", required = true) @PathVariable Long guestId
    ) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        if (!jwtUtil.validateToken(jwt)) {
            throw new IllegalArgumentException("Invalid token");
        }
        String username = jwtUtil.extractUsername(jwt);
        return ResponseCustom.OK(teamService.sendCode(username, guestId));
    }

    @Operation(summary = "참여코드로 팀(방) 참여", description = "참여코드로 팀(방)에 참여합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팀(방) 참여 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "팀(방) 참여 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/join/{joinCode}")
    public ResponseCustom<Message> joinTeam(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @RequestHeader("Authorization") String token,
            @Parameter(description = "참여코드를 입력해주세요.", required = true) @PathVariable String joinCode
    ) {
        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        if (!jwtUtil.validateToken(jwt)) {
            throw new IllegalArgumentException("Invalid token");
        }
        String username = jwtUtil.extractUsername(jwt);
        return ResponseCustom.OK(teamService.join(username, joinCode));
    }

    @Operation(summary = "루틴 목록 조회", description = "teamId로 설정된 루틴 목록 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "루틴 목록 조회 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CreateTeamResponse.class))}),
            @ApiResponse(responseCode = "400", description = "루틴 목록 조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/routine-list")
    public ResponseCustom<findAllRoutineByUserIdResponse> getRoutineList(
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
        return ResponseCustom.OK(teamService.findAllRoutineByUserId(userId));
    }

    @Operation(summary = "팀 참가 유저 명단 조회", description = "팀에 존재하는 유저 명단")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공 ", content = {@Content(mediaType = "application/json", schema = @Schema(implementation =  findByTeamIdResponse.class))}),
            @ApiResponse(responseCode = "400", description = "조회 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/user-list")
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
        return ResponseCustom.OK(teamService.findTeamByUserId(userId));
    }

}
