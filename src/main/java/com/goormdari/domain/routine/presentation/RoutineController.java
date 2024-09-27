package com.goormdari.domain.routine.presentation;


import com.goormdari.domain.routine.application.RoutineService;
import com.goormdari.domain.routine.dto.request.CompleteRoutineRequest;
import com.goormdari.domain.team.dto.request.CreateTeamRequest;
import com.goormdari.domain.team.dto.response.CreateTeamResponse;
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
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Routine", description = "Routine API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/routine")
public class RoutineController {

    private final S3Service s3Service;
    private final RoutineService routineService;

    @Operation(summary = "루틴 완수", description = "사진을 업로드하여 루틴을 완수합니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "루틴 완수 성공", content = {@Content(mediaType = "application/json", schema = @Schema(implementation =  Message.class))}),
            @ApiResponse(responseCode = "400", description = "루틴 완수 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/upload")
    public ResponseCustom<Message> uploadRoutine(
            @Parameter(description = "Accesstoken을 입력해주세요.", required = true) @RequestHeader Long userId,
            @Parameter(description = "Schemas의 completeRoutineRequest를 참고해주세요.", required = true) @Valid @RequestBody CompleteRoutineRequest completeRoutineRequest,
            @Parameter(description = "file 이미지 업로드", required = true) @Valid @RequestParam("file") MultipartFile file
    ) {
        String imgURL = s3Service.uploadImageToS3(file);
        return ResponseCustom.OK(routineService.completeRoutine(userId, completeRoutineRequest, imgURL));
    }


   /* @PostMapping("/upload")
    public ResponseCustom<Message> testUpload(@Valid @RequestParam("file") MultipartFile file) {
        String imgURL = s3Service.uploadImageToS3(file);
        return ResponseCustom.OK(Message.builder().message(imgURL).build());
    }

   @DeleteMapping("/upload")
    public ResponseCustom<Message> testDelete(
            @Valid @RequestParam("imgURL") String imgURL
    ) {
       s3Service.deleteImageOnS3(imgURL);
       return ResponseCustom.OK(Message.builder().message("good").build());
    }*/
}
