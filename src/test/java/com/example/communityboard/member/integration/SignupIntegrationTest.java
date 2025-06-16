package com.example.communityboard.member.integration;

import com.example.communityboard.member.domain.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SignupIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @BeforeEach
    void setUp() {
        // 데이터베이스 초기화는 @Transactional로 자동 처리
    }
    
    @Test
    @DisplayName("회원가입 전체 플로우가 정상적으로 동작한다")
    void signupIntegrationTest() throws Exception {
        // given
        String signupJson = """
                {
                    "loginId": "testuser123",
                    "password": "Password123!",
                    "nickname": "테스트유저",
                    "email": "test@example.com"
                }
                """;
        
        // when & then
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입에 성공했습니다."))
                .andExpect(jsonPath("$.data.memberId").exists())
                .andExpect(jsonPath("$.data.loginId").value("testuser123"))
                .andExpect(jsonPath("$.data.nickname").value("테스트유저"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
        
        // 데이터베이스에 실제로 저장되었는지 확인
        assertThat(memberRepository.existsByLoginId(
                com.example.communityboard.member.domain.vo.LoginId.of("testuser123")
        )).isTrue();
    }
    
    @Test
    @DisplayName("중복된 아이디로 회원가입 시도시 409 CONFLICT를 반환한다")
    void signupWithDuplicateLoginId() throws Exception {
        // given
        String firstSignup = """
                {
                    "loginId": "duplicateuser",
                    "password": "Password123!",
                    "nickname": "첫번째유저",
                    "email": "first@example.com"
                }
                """;
        
        String secondSignup = """
                {
                    "loginId": "duplicateuser",
                    "password": "Password456!",
                    "nickname": "두번째유저",
                    "email": "second@example.com"
                }
                """;
        
        // 첫 번째 회원가입
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstSignup))
                .andExpect(status().isOk());
        
        // when & then - 두 번째 회원가입 시도
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondSignup))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("이미 사용 중인 아이디입니다."));
    }
    
    @Test
    @DisplayName("중복된 이메일로 회원가입 시도시 409 CONFLICT를 반환한다")
    void signupWithDuplicateEmail() throws Exception {
        // given
        String firstSignup = """
                {
                    "loginId": "user1",
                    "password": "Password123!",
                    "nickname": "첫번째유저",
                    "email": "duplicate@example.com"
                }
                """;
        
        String secondSignup = """
                {
                    "loginId": "user2",
                    "password": "Password456!",
                    "nickname": "두번째유저",
                    "email": "duplicate@example.com"
                }
                """;
        
        // 첫 번째 회원가입
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(firstSignup))
                .andExpect(status().isOk());
        
        // when & then - 두 번째 회원가입 시도
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(secondSignup))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("이미 사용 중인 이메일입니다."));
    }
    
    @Test
    @DisplayName("유효하지 않은 입력값으로 회원가입 시도시 400 BAD REQUEST를 반환한다")
    void signupWithInvalidInput() throws Exception {
        // given - 짧은 아이디
        String invalidLoginId = """
                {
                    "loginId": "ab",
                    "password": "Password123!",
                    "nickname": "테스트유저",
                    "email": "test@example.com"
                }
                """;
        
        // when & then
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidLoginId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("아이디는 4자 이상 20자 이하여야 합니다."));
        
        // given - 약한 비밀번호
        String weakPassword = """
                {
                    "loginId": "testuser",
                    "password": "1234",
                    "nickname": "테스트유저",
                    "email": "test@example.com"
                }
                """;
        
        // when & then
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(weakPassword))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("비밀번호는 최소 8자 이상이어야 합니다."));
        
        // given - 잘못된 이메일 형식
        String invalidEmail = """
                {
                    "loginId": "testuser",
                    "password": "Password123!",
                    "nickname": "테스트유저",
                    "email": "invalid-email"
                }
                """;
        
        // when & then
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidEmail))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("유효한 이메일 형식이 아닙니다."));
    }
    
    @Test
    @DisplayName("회원가입 후 로그인이 정상적으로 동작한다")
    void signupAndLoginIntegrationTest() throws Exception {
        // given - 회원가입
        String signupJson = """
                {
                    "loginId": "newuser",
                    "password": "Password123!",
                    "nickname": "새유저",
                    "email": "new@example.com"
                }
                """;
        
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signupJson))
                .andExpect(status().isOk());
        
        // when & then - 로그인
        String loginJson = """
                {
                    "loginId": "newuser",
                    "password": "Password123!"
                }
                """;
        
        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인에 성공했습니다."))
                .andExpect(jsonPath("$.data.loginId").value("newuser"))
                .andExpect(jsonPath("$.data.nickname").value("새유저"))
                .andExpect(jsonPath("$.data.email").value("new@example.com"));
    }
}