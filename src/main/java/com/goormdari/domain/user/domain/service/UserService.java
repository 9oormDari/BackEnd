package com.goormdari.domain.user.domain.service;

import com.goormdari.domain.user.domain.User;
import com.goormdari.domain.user.domain.dto.AddUserRequest;
import com.goormdari.domain.user.domain.dto.JwtResponse;
import com.goormdari.domain.user.domain.dto.LoginRequest;
import com.goormdari.domain.user.domain.repository.UserRepository;
import com.goormdari.global.config.security.jwt.JWTUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    @Transactional
    public Long save(AddUserRequest dto) {
        // 사용자 이름 중복 체크
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username is already exists.");
        }

        // 사용자 저장
        return userRepository.save(User.builder()
                .nickname(dto.getNickname())
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role("ROLE_USER")
                .build()).getId();
    }

    public JwtResponse signupAndLogin(AddUserRequest dto) {
        save(dto);

        return loginAndGetToken(new LoginRequest(dto.getUsername(), dto.getPassword()));
    }

    public JwtResponse loginAndGetToken(LoginRequest loginRequest) {
        // 사용자 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // JWT 생성
        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Username not found with: " + loginRequest.getUsername()));
        String jwt = jwtUtil.generateToken(user.getUsername(), user.getRole());

        return new JwtResponse(jwt);
    }
}
