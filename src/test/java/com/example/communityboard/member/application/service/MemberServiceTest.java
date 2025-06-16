package com.example.communityboard.member.application.service;

import com.example.communityboard.member.application.dto.LoginRequest;
import com.example.communityboard.member.application.dto.LoginResponse;
import com.example.communityboard.member.application.exception.InvalidLoginException;
import com.example.communityboard.member.domain.entity.Member;
import com.example.communityboard.member.domain.repository.MemberRepository;
import com.example.communityboard.member.domain.vo.LoginId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;
    private String rawPassword = "password123!";

    @BeforeEach
    void setUp() {
        // 실제 BCryptPasswordEncoder를 사용하여 테스트 멤버 생성
        PasswordEncoder realEncoder = new BCryptPasswordEncoder();
        testMember = Member.register(
                "testuser",
                rawPassword,
                "테스트유저",
                "test@example.com",
                realEncoder
        );
    }

    @Test
    @DisplayName("정상적인 로그인 요청시 회원 정보를 반환한다")
    void loginSuccess() {
        // given
        LoginRequest request = new LoginRequest("testuser", rawPassword);
        when(memberRepository.findByLoginId(any(LoginId.class)))
                .thenReturn(Optional.of(testMember));
        when(passwordEncoder.matches(eq(rawPassword), any(String.class)))
                .thenReturn(true);

        // when
        LoginResponse response = memberService.login(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getLoginId()).isEqualTo("testuser");
        assertThat(response.getNickname()).isEqualTo("테스트유저");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("존재하지 않는 아이디로 로그인 시도시 예외가 발생한다")
    void loginFailWithInvalidLoginId() {
        // given
        LoginRequest request = new LoginRequest("nonexistent", rawPassword);
        when(memberRepository.findByLoginId(any(LoginId.class)))
                .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(InvalidLoginException.class)
                .hasMessage("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인 시도시 예외가 발생한다")
    void loginFailWithInvalidPassword() {
        // given
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");
        when(memberRepository.findByLoginId(any(LoginId.class)))
                .thenReturn(Optional.of(testMember));
        when(passwordEncoder.matches(eq("wrongpassword"), any(String.class)))
                .thenReturn(false);

        // when & then
        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(InvalidLoginException.class)
                .hasMessage("아이디 또는 비밀번호가 올바르지 않습니다.");
    }
}