package com.goormdari.domain.routine.presentation;


import com.goormdari.domain.calendar.exception.InvalidTokenException;
import com.goormdari.domain.routine.application.RoutineService;
import com.goormdari.domain.routine.domain.Routine;
import com.goormdari.domain.routine.dto.request.CompleteRoutineRequest;
import com.goormdari.domain.user.domain.service.UserService;
import com.goormdari.global.config.security.jwt.JWTUtil;
import com.goormdari.global.config.s3.S3Service;
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

@Tag(name = "Routine", description = "Routine API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/routine")
public class RoutineController {

    private final S3Service s3Service;
    private final RoutineService routineService;
    private final UserService userService;

    private final JWTUtil jwtUtil;

    @Operation(summary = "루틴 완수", description = "사진을 업로드하여 루틴을 완수합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "루틴 완수 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Message.class))}),
            @ApiResponse(responseCode = "400", description = "루틴 완수 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/upload")
    public ResponseCustom<Message> uploadRoutine(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @RequestHeader("Authorization") String token,
            @Parameter(description = "Schemas의 completeRoutineRequest를 참고해주세요.", required = true) @Valid @ModelAttribute CompleteRoutineRequest completeRoutineRequest
    ) {
        if (token == null) {
            throw new InvalidTokenException();
        }

        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        if (!jwtUtil.validateToken(jwt)) {
            throw new IllegalArgumentException("Invalid token");
        }
        Long userId = jwtUtil.extractId(jwt);
        String imgURL = s3Service.uploadImageToS3(completeRoutineRequest.file());
        return ResponseCustom.OK(routineService.completeRoutine(userId, completeRoutineRequest, imgURL));
    }

    @Operation(summary = "루틴 삭제", description = "이미지 url, routineIndex로 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "루틴 완수 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseCustom.class))}),
            @ApiResponse(responseCode = "400", description = "루틴 완수 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @DeleteMapping("/upload")
    public ResponseCustom<Message> deleteRoutine(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @RequestHeader("Authorization") String token,
            @Parameter(description = "이미지 url", required = true) @Valid @RequestParam("imgURL") String imgURL,
            @Parameter(description = "루틴 Index", required = true) @Valid @RequestParam("routineIndex") Long routineIndex
    ) {
        if (token == null) {
            throw new InvalidTokenException();
        }

        String jwt = token.startsWith("Bearer ") ? token.substring(7) : token;
        if (!jwtUtil.validateToken(jwt)) {
            throw new IllegalArgumentException("Invalid token");
        }
        Long userId = jwtUtil.extractId(jwt);
        s3Service.deleteImageOnS3(imgURL);
        return ResponseCustom.OK(routineService.deleteRoutineByUserIdAndRoutineIndex(userId, routineIndex));
    }

    @Operation(summary = "유저별 루틴 전체 조회", description = "userId로 해당 유저 루틴 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "루틴 완수 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ResponseCustom.class))}),
            @ApiResponse(responseCode = "400", description = "루틴 완수 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @GetMapping("/{userId}")
    public ResponseCustom<List<Routine>> getAllRoutineByUserId(
            @Parameter(description = "조회 할 userId", required = true) @PathVariable("userId") Long userId
    ) {
        return ResponseCustom.OK(routineService.findAllRoutineByUserId(userId));
    }

}
