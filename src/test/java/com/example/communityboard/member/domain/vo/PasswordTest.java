package com.example.communityboard.member.domain.vo;

import com.example.communityboard.member.domain.vo.Password;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordTest {

    @Test
    @DisplayName("정상적인 비밀번호 생성")
    void createPassword_Success() {
        // given
        String validPassword = "Password123!";

        // when
        Password password = Password.of(validPassword);

        // then
        assertThat(password.getValue()).isEqualTo(validPassword);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "Password1!",      // 영문 + 숫자 + 특수문자
            "Password123",     // 영문 + 숫자
            "Password!!",      // 영문 + 특수문자
            "12345678!!",      // 숫자 + 특수문자
            "Abcdefgh1",       // 최소 조건 만족
            "!@#$%^&*()123"    // 특수문자 + 숫자
    })
    @DisplayName("유효한 비밀번호들")
    void createPassword_ValidCases(String validPassword) {
        // when
        Password password = Password.of(validPassword);

        // then
        assertThat(password.getValue()).isEqualTo(validPassword);
    }

    @Test
    @DisplayName("null 값으로 생성 시 예외 발생")
    void createPassword_NullValue_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Password.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 필수입니다.");
    }

    @Test
    @DisplayName("빈 문자열로 생성 시 예외 발생")
    void createPassword_EmptyValue_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Password.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 필수입니다.");
    }

    @Test
    @DisplayName("8자 미만으로 생성 시 예외 발생")
    void createPassword_TooShort_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Password.of("Pass1!"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 최소 8자 이상이어야 합니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "password",        // 영문만
            "12345678",        // 숫자만
            "!!!!!!!!",        // 특수문자만
            "Password",        // 영문 대소문자만
            "passwordonly"     // 영문만
    })
    @DisplayName("문자 종류가 2개 미만일 때 예외 발생")
    void createPassword_InsufficientCharTypes_ThrowsException(String invalidPassword) {
        // when & then
        assertThatThrownBy(() -> Password.of(invalidPassword))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 영문, 숫자, 특수문자 중 2종류 이상 포함해야 합니다.");
    }

    @Test
    @DisplayName("비밀번호 일치 확인 - 같은 비밀번호")
    void match_SamePassword_ReturnsTrue() {
        // given
        String rawPassword = "Password123!";
        Password password = Password.of(rawPassword);

        // when & then
        assertThat(password.match(rawPassword)).isTrue();
    }

    @Test
    @DisplayName("비밀번호 일치 확인 - 다른 비밀번호")
    void match_DifferentPassword_ReturnsFalse() {
        // given
        Password password = Password.of("Password123!");

        // when & then
        assertThat(password.match("WrongPassword456!")).isFalse();
    }

    @Test
    @DisplayName("동등성 비교 - 같은 값")
    void equals_SameValue_ReturnsTrue() {
        // given
        Password password1 = Password.of("Password123!");
        Password password2 = Password.of("Password123!");

        // when & then
        assertThat(password1).isEqualTo(password2);
        assertThat(password1.hashCode()).isEqualTo(password2.hashCode());
    }

    @Test
    @DisplayName("동등성 비교 - 다른 값")
    void equals_DifferentValue_ReturnsFalse() {
        // given
        Password password1 = Password.of("Password123!");
        Password password2 = Password.of("Password456!");

        // when & then
        assertThat(password1).isNotEqualTo(password2);
    }
}