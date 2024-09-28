package com.goormdari.domain.user.presentation;

import com.goormdari.domain.user.dto.request.AddUserRequest;
import com.goormdari.domain.user.dto.response.FindCurrentStepResponse;
import com.goormdari.domain.user.dto.response.JwtResponse;
import com.goormdari.domain.user.dto.request.LoginRequest;
import com.goormdari.domain.user.service.UserService;

import com.goormdari.global.payload.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authorization", description = "Authorization API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @Operation(summary = "회원가입 후 로그인", description = "회원가입과 로그인을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공 ", content = {@Content(mediaType = "application/json", schema = @Schema(implementation =  JwtResponse.class))}),
            @ApiResponse(responseCode = "400", description = "회원가입 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/signup")
    public ResponseEntity<JwtResponse> registerUser(@Valid @RequestBody AddUserRequest addUserRequest) {
        JwtResponse jwtResponse = userService.signupAndLogin(addUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(jwtResponse);
    }

    @Operation(summary = "로그인", description = "로그인을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공 ", content = {@Content(mediaType = "application/json", schema = @Schema(implementation =  JwtResponse.class))}),
            @ApiResponse(responseCode = "400", description = "로그인 실패", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))}),
    })
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = userService.loginAndGetToken(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }
}
