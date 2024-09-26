package com.goormdari.domain.team.presentation;

import com.goormdari.domain.team.application.TeamService;
import com.goormdari.domain.team.dto.request.CreateTeamRequest;
import com.goormdari.domain.team.dto.response.CreateTeamResponse;
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

@Tag(name = "Team", description = "Team API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "팀(방) 생성", description = "팀(방)을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팀(방) 생성 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = CreateTeamResponse.class))}),
            @ApiResponse(responseCode = "400", description = "팀(방) 생성 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping
    public ResponseCustom<CreateTeamResponse> createTeam(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @RequestHeader Long userId,
            @Parameter(description = "Schemas의 CreateTeamRequest를 참고해주세요.", required = true) @Valid @RequestBody CreateTeamRequest createTeamRequest
    ) {
        return ResponseCustom.OK(teamService.createNewTeam(userId, createTeamRequest));
    }

    @Operation(summary = "팀(방) 참여코드 전달", description = "팀(방) 참여코드를 이메일로 전달합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "팀(방) 참여코드 전달 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "팀(방) 참여코드 전달 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/email/{guestId}")
    public ResponseCustom<Message> sendJoinCode(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @RequestHeader Long userId,
            @Parameter(description = "초대할 유저의 id를 입력해주세요.", required = true) @PathVariable Long guestId
    ) {
        return ResponseCustom.OK(teamService.sendCode(userId, guestId));
    }
}
