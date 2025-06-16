package com.example.communityboard.domain.member.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MemberTest {

    @Test
    @DisplayName("정상적인 회원 생성")
    void createMember_Success() {
        // given
        String loginId = "testuser123";
        String password = "Password123!";
        String nickname = "테스터";
        String email = "test@example.com";

        // when
        Member member = Member.register(loginId, password, nickname, email);

        // then
        assertThat(member).isNotNull();
        assertThat(member.getLoginId().getValue()).isEqualTo(loginId);
        assertThat(member.getNickname().getValue()).isEqualTo(nickname);
        assertThat(member.getEmail().getValue()).isEqualTo(email);
    }

    @Test
    @DisplayName("잘못된 로그인 아이디로 회원 생성 시 예외 발생")
    void createMember_InvalidLoginId_ThrowsException() {
        // given
        String invalidLoginId = "ab";  // 4자 미만
        String password = "Password123!";
        String nickname = "테스터";
        String email = "test@example.com";

        // when & then
        assertThatThrownBy(() -> Member.register(invalidLoginId, password, nickname, email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("아이디는 4자 이상 20자 이하여야 합니다.");
    }

    @Test
    @DisplayName("잘못된 비밀번호로 회원 생성 시 예외 발생")
    void createMember_InvalidPassword_ThrowsException() {
        // given
        String loginId = "testuser123";
        String invalidPassword = "1234";  // 8자 미만, 문자 종류 부족
        String nickname = "테스터";
        String email = "test@example.com";

        // when & then
        assertThatThrownBy(() -> Member.register(loginId, invalidPassword, nickname, email))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호는 최소 8자 이상이어야 합니다.");
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void changePassword_Success() {
        // given
        Member member = Member.register("testuser123", "OldPassword123!", "테스터", "test@example.com");
        String newPassword = "NewPassword456!";

        // when
        member.changePassword(newPassword);

        // then
        assertThat(member.getPassword().matches(newPassword)).isTrue();
    }

    @Test
    @DisplayName("잘못된 비밀번호로 변경 시 예외 발생")
    void changePassword_InvalidPassword_ThrowsException() {
        // given
        Member member = Member.register("testuser123", "OldPassword123!", "테스터", "test@example.com");
        String invalidPassword = "short";

        // when & then
        assertThatThrownBy(() -> member.changePassword(invalidPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("비밀번호는 최소 8자 이상이어야 합니다.");
    }

    @Test
    @DisplayName("닉네임 변경 성공")
    void changeNickname_Success() {
        // given
        Member member = Member.register("testuser123", "Password123!", "테스터", "test@example.com");
        String newNickname = "새닉네임";

        // when
        member.changeNickname(newNickname);

        // then
        assertThat(member.getNickname().getValue()).isEqualTo(newNickname);
    }

    @Test
    @DisplayName("이메일 변경 성공")
    void changeEmail_Success() {
        // given
        Member member = Member.register("testuser123", "Password123!", "테스터", "test@example.com");
        String newEmail = "newemail@example.com";

        // when
        member.changeEmail(newEmail);

        // then
        assertThat(member.getEmail().getValue()).isEqualTo(newEmail);
    }
}