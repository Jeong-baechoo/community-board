package com.example.communityboard.domain.member.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NicknameTest {

    @Test
    @DisplayName("정상적인 닉네임 생성")
    void createNickname_Success() {
        // given
        String validNickname = "테스터";

        // when
        Nickname nickname = Nickname.of(validNickname);

        // then
        assertThat(nickname.getValue()).isEqualTo(validNickname);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "ab",              // 영문 2자
            "가나",            // 한글 2자
            "12",              // 숫자 2자
            "테스터123",       // 한글 + 숫자
            "user테스터",      // 영문 + 한글
            "가나다라마바사아자차" // 한글 10자
    })
    @DisplayName("유효한 닉네임들")
    void createNickname_ValidCases(String validNickname) {
        // when
        Nickname nickname = Nickname.of(validNickname);

        // then
        assertThat(nickname.getValue()).isEqualTo(validNickname);
    }

    @Test
    @DisplayName("null 값으로 생성 시 예외 발생")
    void createNickname_NullValue_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Nickname.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("닉네임은 필수입니다.");
    }

    @Test
    @DisplayName("빈 문자열로 생성 시 예외 발생")
    void createNickname_EmptyValue_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Nickname.of(""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("닉네임은 필수입니다.");
    }

    @Test
    @DisplayName("2자 미만으로 생성 시 예외 발생")
    void createNickname_TooShort_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Nickname.of("a"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("닉네임은 2자 이상 10자 이하여야 합니다.");
    }

    @Test
    @DisplayName("10자 초과로 생성 시 예외 발생")
    void createNickname_TooLong_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> Nickname.of("가나다라마바사아자차카"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("닉네임은 2자 이상 10자 이하여야 합니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "닉네임!",         // 특수문자 포함
            "nick name",       // 공백 포함
            "닉네임@",         // @ 포함
            "nick_name",       // 언더스코어 포함
            "nick-name"        // 하이픈 포함
    })
    @DisplayName("허용되지 않는 문자 포함 시 예외 발생")
    void createNickname_InvalidCharacters_ThrowsException(String invalidNickname) {
        // when & then
        assertThatThrownBy(() -> Nickname.of(invalidNickname))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("닉네임은 한글, 영문, 숫자만 사용 가능합니다.");
    }

    @Test
    @DisplayName("동등성 비교 - 같은 값")
    void equals_SameValue_ReturnsTrue() {
        // given
        Nickname nickname1 = Nickname.of("테스터");
        Nickname nickname2 = Nickname.of("테스터");

        // when & then
        assertThat(nickname1).isEqualTo(nickname2);
        assertThat(nickname1.hashCode()).isEqualTo(nickname2.hashCode());
    }

    @Test
    @DisplayName("동등성 비교 - 다른 값")
    void equals_DifferentValue_ReturnsFalse() {
        // given
        Nickname nickname1 = Nickname.of("테스터1");
        Nickname nickname2 = Nickname.of("테스터2");

        // when & then
        assertThat(nickname1).isNotEqualTo(nickname2);
    }
}