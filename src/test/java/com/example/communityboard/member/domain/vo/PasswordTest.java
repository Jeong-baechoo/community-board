package com.example.communityboard.member.domain.vo;

import com.example.communityboard.member.domain.vo.Password;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PasswordTest {

    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    @DisplayName("정상적인 비밀번호 생성 - 암호화")
    void createPassword_WithEncryption_Success() {
        // given
        String rawPassword = "Password123!";

        // when
        Password password = Password.ofRaw(rawPassword, passwordEncoder);

        // then
        assertThat(password.getValue()).isNotEqualTo(rawPassword);
        assertThat(password.isEncrypted()).isTrue();
        assertThat(password.match(rawPassword, passwordEncoder)).isTrue();
    }

    @Test
    @DisplayName("암호화된 비밀번호로 생성")
    void createPassword_FromEncrypted_Success() {
        // given
        String encryptedPassword = "$2a$10$abcdefghijk";

        // when
        Password password = Password.ofEncrypted(encryptedPassword);

        // then
        assertThat(password.getValue()).isEqualTo(encryptedPassword);
        assertThat(password.isEncrypted()).isTrue();
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
    @DisplayName("유효한 비밀번호들 - 암호화")
    void createPassword_ValidCases_WithEncryption(String validPassword) {
        // when
        Password password = Password.ofRaw(validPassword, passwordEncoder);

        // then
        assertThat(password.isEncrypted()).isTrue();
        assertThat(password.match(validPassword, passwordEncoder)).isTrue();
    }

    @Test
    @DisplayName("null 값으로 생성 시 예외 발생")
    void createPassword_NullValue_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Password.ofRaw(null, passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 필수입니다.");
    }

    @Test
    @DisplayName("빈 문자열로 생성 시 예외 발생")
    void createPassword_EmptyValue_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Password.ofRaw("", passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 필수입니다.");
    }

    @Test
    @DisplayName("8자 미만으로 생성 시 예외 발생")
    void createPassword_TooShort_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Password.ofRaw("Pass1!", passwordEncoder))
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
        assertThatThrownBy(() -> Password.ofRaw(invalidPassword, passwordEncoder))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호는 영문, 숫자, 특수문자 중 2종류 이상 포함해야 합니다.");
    }

    @Test
    @DisplayName("비밀번호 일치 확인 - 같은 비밀번호")
    void match_SamePassword_ReturnsTrue() {
        // given
        String rawPassword = "Password123!";
        Password password = Password.ofRaw(rawPassword, passwordEncoder);

        // when & then
        assertThat(password.match(rawPassword, passwordEncoder)).isTrue();
    }

    @Test
    @DisplayName("비밀번호 일치 확인 - 다른 비밀번호")
    void match_DifferentPassword_ReturnsFalse() {
        // given
        String rawPassword = "Password123!";
        Password password = Password.ofRaw(rawPassword, passwordEncoder);

        // when & then
        assertThat(password.match("WrongPassword456!", passwordEncoder)).isFalse();
    }

    @Test
    @DisplayName("비밀번호 일치 확인 - null 입력")
    void match_NullInput_ReturnsFalse() {
        // given
        Password password = Password.ofRaw("Password123!", passwordEncoder);

        // when & then
        assertThat(password.match(null, passwordEncoder)).isFalse();
    }

    @Test
    @DisplayName("암호화 여부 확인 - BCrypt 형식")
    void isEncrypted_BcryptFormat_ReturnsTrue() {
        // given
        String bcryptPassword1 = "$2a$10$abcdefghijk";
        String bcryptPassword2 = "$2b$10$abcdefghijk";
        String bcryptPassword3 = "$2y$10$abcdefghijk";

        // when
        Password password1 = Password.ofEncrypted(bcryptPassword1);
        Password password2 = Password.ofEncrypted(bcryptPassword2);
        Password password3 = Password.ofEncrypted(bcryptPassword3);

        // then
        assertThat(password1.isEncrypted()).isTrue();
        assertThat(password2.isEncrypted()).isTrue();
        assertThat(password3.isEncrypted()).isTrue();
    }

    @Test
    @DisplayName("암호화 여부 확인 - 일반 텍스트")
    void isEncrypted_PlainText_ReturnsFalse() {
        // given
        String plainPassword = "PlainPassword123!";

        // when
        Password password = Password.ofEncrypted(plainPassword);

        // then
        assertThat(password.isEncrypted()).isFalse();
    }

    @Test
    @DisplayName("동등성 비교는 암호화된 값으로")
    void equals_WithEncryptedValues() {
        // given
        String rawPassword = "Password123!";
        String encryptedValue = passwordEncoder.encode(rawPassword);
        
        Password password1 = Password.ofEncrypted(encryptedValue);
        Password password2 = Password.ofEncrypted(encryptedValue);

        // when & then
        assertThat(password1).isEqualTo(password2);
        assertThat(password1.hashCode()).isEqualTo(password2.hashCode());
    }
}