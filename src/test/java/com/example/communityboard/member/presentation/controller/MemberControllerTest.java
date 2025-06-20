package com.example.communityboard.member.presentation.controller;

import com.example.communityboard.member.application.dto.LoginRequest;
import com.example.communityboard.member.application.dto.LoginResponse;
import com.example.communityboard.member.application.dto.SignupRequest;
import com.example.communityboard.member.application.dto.SignupResponse;
import com.example.communityboard.member.application.exception.DuplicateLoginIdException;
import com.example.communityboard.member.application.exception.InvalidLoginException;
import com.example.communityboard.member.application.service.MemberService;
import com.example.communityboard.common.config.TestSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MemberController.class)
@Import(TestSecurityConfig.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("로그인 성공시 200 OK와 회원 정보를 반환한다")
    @WithMockUser
    void loginSuccess() throws Exception {
        // given
        LoginRequest request = new LoginRequest("testuser", "password123!");
        LoginResponse response = new LoginResponse(1L, "testuser", "테스트유저", "test@example.com");
        
        given(memberService.login(any(LoginRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("로그인에 성공했습니다."))
                .andExpect(jsonPath("$.data.memberId").value(1))
                .andExpect(jsonPath("$.data.loginId").value("testuser"))
                .andExpect(jsonPath("$.data.nickname").value("테스트유저"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    @DisplayName("로그인 실패시 401 UNAUTHORIZED를 반환한다")
    @WithMockUser
    void loginFail() throws Exception {
        // given
        LoginRequest request = new LoginRequest("wronguser", "wrongpassword");
        
        given(memberService.login(any(LoginRequest.class)))
                .willThrow(new InvalidLoginException());

        // when & then
        mockMvc.perform(post("/api/members/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("아이디 또는 비밀번호가 올바르지 않습니다."));
    }
    
    @Test
    @DisplayName("회원가입 성공시 200 OK와 회원 정보를 반환한다")
    @WithMockUser
    void signupSuccess() throws Exception {
        // given
        SignupRequest request = new SignupRequest("newuser", "Password123!", "새유저", "new@example.com");
        SignupResponse response = new SignupResponse(1L, "newuser", "새유저", "new@example.com");
        
        given(memberService.signup(any(SignupRequest.class))).willReturn(response);
        
        // when & then
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입에 성공했습니다."))
                .andExpect(jsonPath("$.data.memberId").value(1))
                .andExpect(jsonPath("$.data.loginId").value("newuser"))
                .andExpect(jsonPath("$.data.nickname").value("새유저"))
                .andExpect(jsonPath("$.data.email").value("new@example.com"));
    }
    
    @Test
    @DisplayName("중복된 아이디로 회원가입 시도시 409 CONFLICT를 반환한다")
    @WithMockUser
    void signupFailWithDuplicateLoginId() throws Exception {
        // given
        SignupRequest request = new SignupRequest("existinguser", "Password123!", "새유저", "new@example.com");
        
        given(memberService.signup(any(SignupRequest.class)))
                .willThrow(new DuplicateLoginIdException());
        
        // when & then
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("이미 사용 중인 아이디입니다."));
    }
    
    @Test
    @DisplayName("유효하지 않은 입력값으로 회원가입 시도시 400 BAD_REQUEST를 반환한다")
    @WithMockUser
    void signupFailWithInvalidInput() throws Exception {
        // given - 아이디가 너무 짧은 경우
        String invalidRequest = """
                {
                    "loginId": "a",
                    "password": "Password123!",
                    "nickname": "새유저",
                    "email": "new@example.com"
                }
                """;
        
        // when & then
        mockMvc.perform(post("/api/members/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }
}