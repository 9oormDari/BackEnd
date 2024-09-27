package com.goormdari;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goormdari.domain.user.domain.User;
import com.goormdari.domain.user.domain.dto.AddUserRequest;
import com.goormdari.domain.user.domain.dto.LoginRequest;
import com.goormdari.domain.user.domain.repository.UserRepository;
import com.goormdari.global.config.security.jwt.JWTUtil;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc // 테스트 프로파일 사용
@Transactional // 테스트 후 롤백
class GoormDariApplicationTests {

    @Autowired
    private MockMvc mockMvc; // @Autowired 추가

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    @Test
    public void testRegisterUser_Success() throws Exception {
        AddUserRequest dto = new AddUserRequest();
        dto.setNickname("testnick");
        dto.setUsername("testuser");
        dto.setPassword("password123");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated()) // 201 Created
                .andExpect(jsonPath("$.token").exists()); // JwtResponse의 필드에 맞게 수정
    }

    @Test
    public void testRegisterUser_DuplicateUsername() throws Exception {
        // 이미 존재하는 사용자 생성
        User existingUser = User.builder()
                .nickname("existingNick")
                .username("existingUser")
                .password(passwordEncoder.encode("password123"))
                .role("ROLE_USER")
                .build();
        userRepository.save(existingUser);

        AddUserRequest dto = new AddUserRequest();
        dto.setNickname("newNick");
        dto.setUsername("existingUser");
        dto.setPassword("newpassword");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest()) // 400 Bad Request
                .andExpect(jsonPath("$.check").value(false));
    }

    @Test
    public void testLoginUser_Success() throws Exception {
        // 사용자 등록
        User user = User.builder()
                .nickname("loginNick")
                .username("loginUser")
                .password(passwordEncoder.encode("password123"))
                .role("ROLE_USER")
                .build();
        userRepository.save(user);

        LoginRequest loginDto = new LoginRequest();
        loginDto.setUsername("loginUser");
        loginDto.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.token").exists()); // JwtResponse의 필드에 맞게 수정
    }

    @Test
    public void testLoginUser_InvalidCredentials() throws Exception {
        // 사용자 등록
        User user = User.builder()
                .nickname("loginNick")
                .username("loginUser")
                .password(passwordEncoder.encode("password123"))
                .role("ROLE_USER")
                .build();
        userRepository.save(user);

        LoginRequest loginDto = new LoginRequest();
        loginDto.setUsername("loginUser");
        loginDto.setPassword("wrongpassword"); // 잘못된 비밀번호

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)))
                .andExpect(status().isUnauthorized()) // 401 Unauthorized
                .andExpect(jsonPath("$.check").value(false))
                .andExpect(jsonPath("$.information.message").value("Invalid username or password"))
                .andExpect(jsonPath("$.information.status").value(401))
                .andExpect(jsonPath("$.information.code").value("AUTHENTICATION_FAILED"))
                .andExpect(jsonPath("$.information.timestamp").exists());
    }

    @Test
    public void testAccessProtectedEndpoint_WithValidToken() throws Exception {
        // 사용자 등록
        User user = User.builder()
                .nickname("protectedNick")
                .username("protectedUser")
                .password(passwordEncoder.encode("password123"))
                .role("ROLE_USER")
                .build();
        userRepository.save(user);

        // JWT 토큰 생성
        String token = jwtUtil.generateToken("protectedUser", "ROLE_USER"); // User 객체 전달

        // 보호된 엔드포인트에 접근
        mockMvc.perform(post("/user/some-protected-endpoint")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(content().string("Access Granted to Protected Endpoint"));
    }

    @Test
    public void testAccessProtectedEndpoint_WithoutToken() throws Exception {
        // 보호된 엔드포인트에 JWT 없이 접근
        mockMvc.perform(post("/user/some-protected-endpoint"))
                .andExpect(status().isUnauthorized()) // 401 Unauthorized
                .andExpect(jsonPath("$.check").value(false))
                .andExpect(jsonPath("$.information.message").value("AUTHENTICATION_FAILED"))
                .andExpect(jsonPath("$.information.status").value(401))
                .andExpect(jsonPath("$.information.code").value("AUTHENTICATION_FAILED"))
                .andExpect(jsonPath("$.information.timestamp").exists());
    }

    @Test
    public void testAccessProtectedEndpoint_WithInvalidToken() throws Exception {
        // 잘못된 JWT 토큰
        String invalidToken = "invalid.token.here";

        mockMvc.perform(post("/user/some-protected-endpoint")
                        .header("Authorization", "Bearer " + invalidToken))
                .andExpect(status().isUnauthorized()) // 401 Unauthorized
                .andExpect(jsonPath("$.check").value(false))
                .andExpect(jsonPath("$.information.message").value("Invalid access token"))
                .andExpect(jsonPath("$.information.status").value(401))
                .andExpect(jsonPath("$.information.code").value("AUTHENTICATION_FAILED"))
                .andExpect(jsonPath("$.information.timestamp").exists());
    }
}
