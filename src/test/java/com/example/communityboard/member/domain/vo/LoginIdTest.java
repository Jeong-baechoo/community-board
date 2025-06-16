package com.example.communityboard.member.domain.vo;

import com.example.communityboard.member.domain.vo.LoginId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LoginIdTest {

    @Test
    @DisplayName("정상적인 로그인 아이디 생성")
    void createLoginId_Success() {
        // given
        String validId = "user123";

        // when
        LoginId loginId = LoginId.of(validId);

        // then
        assertThat(loginId.getValue()).isEqualTo(validId);
    }

    @ParameterizedTest
    @ValueSource(strings = {"user", "test1234", "abcd1234567890123456"})
    @DisplayName("유효한 로그인 아이디들")
    void createLoginId_ValidCases(String validId) {
        // when
        LoginId loginId = LoginId.of(validId);

        // then
        assertThat(loginId.getValue()).isEqualTo(validId);
    }

    @Test
    @DisplayName("null 값으로 생성 시 예외 발생")
    void createLoginId_NullValue_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> LoginId.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디는 필수입니다.");
    }

    @Test
    @DisplayName("빈 문자열로 생성 시 예외 발생")
    void createLoginId_EmptyValue_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> LoginId.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디는 필수입니다.");
    }

    @Test
    @DisplayName("공백 문자열로 생성 시 예외 발생")
    void createLoginId_BlankValue_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> LoginId.of("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디는 필수입니다.");
    }

    @Test
    @DisplayName("4자 미만으로 생성 시 예외 발생")
    void createLoginId_TooShort_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> LoginId.of("abc"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디는 4자 이상 20자 이하여야 합니다.");
    }

    @Test
    @DisplayName("20자 초과로 생성 시 예외 발생")
    void createLoginId_TooLong_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> LoginId.of("abcdefghijklmnopqrstuvwxyz"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디는 4자 이상 20자 이하여야 합니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"user@123", "user 123", "user_123", "유저123", "user!123"})
    @DisplayName("허용되지 않는 문자 포함 시 예외 발생")
    void createLoginId_InvalidCharacters_ThrowsException(String invalidId) {
        // when & then
        assertThatThrownBy(() -> LoginId.of(invalidId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("아이디는 영문과 숫자만 사용 가능합니다.");
    }

    @Test
    @DisplayName("동등성 비교 - 같은 값")
    void equals_SameValue_ReturnsTrue() {
        // given
        LoginId loginId1 = LoginId.of("user123");
        LoginId loginId2 = LoginId.of("user123");

        // when & then
        assertThat(loginId1).isEqualTo(loginId2);
        assertThat(loginId1.hashCode()).isEqualTo(loginId2.hashCode());
    }

    @Test
    @DisplayName("동등성 비교 - 다른 값")
    void equals_DifferentValue_ReturnsFalse() {
        // given
        LoginId loginId1 = LoginId.of("user123");
        LoginId loginId2 = LoginId.of("user456");

        // when & then
        assertThat(loginId1).isNotEqualTo(loginId2);
    }
}