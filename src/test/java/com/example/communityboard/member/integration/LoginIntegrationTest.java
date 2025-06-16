package com.example.communityboard.member.integration;

import com.example.communityboard.member.domain.entity.Member;
import com.example.communityboard.member.domain.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoginIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        // 테스트용 회원 데이터 생성
        Member member = Member.register(
                "testuser",
                "password123!",
                "테스트유저",
                "test@example.com",
                passwordEncoder
        );
        memberRepository.save(member);
    }

    @Test
    @DisplayName("올바른 아이디와 비밀번호로 로그인하면 회원 정보를 반환한다")
    void loginSuccess() throws Exception {
        // given
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("loginId", "testuser");
        loginRequest.put("password", "password123!");

        // when & then
        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인에 성공했습니다."))
                .andExpect(jsonPath("$.data.loginId").value("testuser"))
                .andExpect(jsonPath("$.data.nickname").value("테스트유저"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.memberId").exists());
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 로그인하면 401 에러를 반환한다")
    void loginFailWithWrongLoginId() throws Exception {
        // given
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("loginId", "wronguser");
        loginRequest.put("password", "password123!");

        // when & then
        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인하면 401 에러를 반환한다")
    void loginFailWithWrongPassword() throws Exception {
        // given
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("loginId", "testuser");
        loginRequest.put("password", "wrongpassword123!");

        // when & then
        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    @DisplayName("필수 파라미터가 누락되면 400 에러를 반환한다")
    void loginFailWithMissingParameter() throws Exception {
        // given
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("loginId", "testuser");
        // password 누락

        // when & then
        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("password: 비밀번호는 필수입니다"));
    }
}