package com.goormdari.domain.user.service;

import com.amazonaws.services.kms.model.NotFoundException;
import com.goormdari.domain.user.domain.exception.DuplicateUsernameException;
import com.goormdari.domain.user.domain.exception.InvalidPasswordException;
import com.goormdari.domain.user.dto.response.UserInfoResponse;
import com.goormdari.domain.user.dto.response.FindCurrentStepResponse;
import com.goormdari.domain.user.domain.User;
import com.goormdari.domain.user.domain.DefaultProfileUrl;
import com.goormdari.domain.user.dto.request.AddUserRequest;
import com.goormdari.domain.user.dto.request.LoginRequest;
import com.goormdari.domain.user.dto.response.JwtResponse;
import com.goormdari.domain.user.domain.repository.UserRepository;
import com.goormdari.global.config.s3.S3Service;
import com.goormdari.domain.validation.annotation.ExistUser;
import com.goormdari.global.config.security.jwt.JWTUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final S3Service s3Service;
    private final JWTUtil jwtUtil;


    @Transactional
    public FindCurrentStepResponse findCurrentStepById(@ExistUser final Long userId) {
//        User user = userRepository.findById(userId).get();

        int count = userRepository.countUserWithRoutines(userId);

        return FindCurrentStepResponse.builder().currentStep(count).build();
    }

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
                .profileUrl(DefaultProfileUrl.getRandomProfileUrl())
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
        String jwt = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());

        return new JwtResponse(jwt);
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundException("User Not Found: " + userId));

        return UserInfoResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileUrl(user.getProfileUrl())
                .goal(user.getGoal())
                .deadline(user.getDeadLine())
                .teamId(user.getTeam() != null ? user.getTeam().getId() : null)
                .build();

    }

    public boolean verifyCurrentPassword(User user, String rawPassword) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }

    @Transactional
    public UserInfoResponse updateUserProfile(Long userId, com.goormdari.domain.user.dto.request.UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(userId).orElseThrow(()->new NotFoundException("User Not Found: " + userId));

        // 닉네임 업데이트
        if (updateUserRequest.getNickname() != null && !updateUserRequest.getNickname().isEmpty()) {
            user.updateNickname(updateUserRequest.getNickname());
        }

        // 유저네임 업데이트 (중복 체크 필요)
        if (updateUserRequest.getUsername() != null && !updateUserRequest.getUsername().isEmpty()) {
            if (userRepository.findByUsername(updateUserRequest.getUsername()).isPresent() &&
                    !user.getUsername().equals(updateUserRequest.getUsername())) {
                throw new DuplicateUsernameException("Username is already exists.");
            }
            user.updateUsername(updateUserRequest.getUsername());
        }

        // 비밀번호 업데이트 (인코딩 필요)
        if (updateUserRequest.getPassword() != null && !updateUserRequest.getPassword().isEmpty()) {
            // 현재 비밀번호 검증
            if (updateUserRequest.getCurrentPassword() == null || !verifyCurrentPassword(user, updateUserRequest.getCurrentPassword())) {
                throw new InvalidPasswordException("Current password is incorrect.");
            }
            user.updatePassword(passwordEncoder.encode(updateUserRequest.getPassword()));
        }

        // 프로필 URL 업데이트
        if (updateUserRequest.getFile() != null && !updateUserRequest.getFile().isEmpty()) {
            String profileUrl = s3Service.uploadImageToS3(updateUserRequest.getFile());
            user.updateProfileUrl(profileUrl);
        }

        // 프로필 이메일 업데이트
        if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().isEmpty()) {
            user.updateEmail(updateUserRequest.getEmail());
        }

        userRepository.save(user);

        return getUserInfo(userId);
    }
}