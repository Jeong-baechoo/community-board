package com.example.communityboard.member.domain.vo;

import com.example.communityboard.member.domain.vo.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    @DisplayName("정상적인 이메일 생성")
    void createEmail_Success() {
        // given
        String validEmail = "test@example.com";

        // when
        Email email = Email.of(validEmail);

        // then
        assertThat(email.getValue()).isEqualTo(validEmail);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "user@domain.com",
            "test.email@example.com",
            "test+tag@example.co.kr",
            "user123@test-domain.com",
            "firstname.lastname@company.org",
            "a@b.co"
    })
    @DisplayName("유효한 이메일 형식들")
    void createEmail_ValidCases(String validEmail) {
        // when
        Email email = Email.of(validEmail);

        // then
        assertThat(email.getValue()).isEqualTo(validEmail);
    }

    @Test
    @DisplayName("null 값으로 생성 시 예외 발생")
    void createEmail_NullValue_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Email.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일은 필수입니다.");
    }

    @Test
    @DisplayName("빈 문자열로 생성 시 예외 발생")
    void createEmail_EmptyValue_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Email.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이메일은 필수입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid.email",      // @ 없음
            "@example.com",       // 로컬 파트 없음
            "user@",              // 도메인 없음
            "user name@example.com", // 공백 포함
            "user@domain",        // TLD 없음
            "user@@example.com",  // @ 중복
            ".user@example.com",  // 점으로 시작
            "user.@example.com",  // 점으로 끝남
            "user@.example.com",  // 도메인이 점으로 시작
            "user@example..com"   // 연속된 점
    })
    @DisplayName("잘못된 이메일 형식으로 생성 시 예외 발생")
    void createEmail_InvalidFormat_ThrowsException(String invalidEmail) {
        // when & then
        assertThatThrownBy(() -> Email.of(invalidEmail))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효한 이메일 형식이 아닙니다.");
    }

    @Test
    @DisplayName("동등성 비교 - 같은 값")
    void equals_SameValue_ReturnsTrue() {
        // given
        Email email1 = Email.of("test@example.com");
        Email email2 = Email.of("test@example.com");

        // when & then
        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }

    @Test
    @DisplayName("동등성 비교 - 다른 값")
    void equals_DifferentValue_ReturnsFalse() {
        // given
        Email email1 = Email.of("test1@example.com");
        Email email2 = Email.of("test2@example.com");

        // when & then
        assertThat(email1).isNotEqualTo(email2);
    }
}