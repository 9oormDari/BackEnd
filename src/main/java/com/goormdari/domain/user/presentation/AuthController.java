package com.goormdari.domain.user.presentation;

import com.goormdari.domain.user.domain.dto.AddUserRequest;
import com.goormdari.domain.user.domain.dto.JwtResponse;
import com.goormdari.domain.user.domain.dto.LoginRequest;
import com.goormdari.domain.user.domain.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 회원가입 후 JWT 토큰 발급
     *
     * @param addUserRequest 회원가입 요청 데이터
     * @return JWT 응답
     */
    @PostMapping("/signup")
    public ResponseEntity<JwtResponse> registerUser(@Valid @RequestBody AddUserRequest addUserRequest) {
        JwtResponse jwtResponse = userService.signupAndLogin(addUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(jwtResponse);
    }

    /**
     * 로그인 후 JWT 토큰 발급
     *
     * @param loginRequest 로그인 요청 데이터
     * @return JWT 응답
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = userService.loginAndGetToken(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }
}
