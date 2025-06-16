package com.example.communityboard.domain.member.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginId {

    @Column(name = "login_id", nullable = false, unique = true)
    private String value;

    private LoginId(String value) {
        validate(value);
        this.value = value;
    }

    public static LoginId of(String value) {
        return new LoginId(value);
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("아이디는 필수입니다.");
        }
        if (value.length() < 4 || value.length() > 20) {
            throw new IllegalArgumentException("아이디는 4자 이상 20자 이하여야 합니다.");
        }
        if (!value.matches("^[a-zA-Z0-9]+$")) {
            throw new IllegalArgumentException("아이디는 영문과 숫자만 사용 가능합니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginId loginId = (LoginId) o;
        return Objects.equals(value, loginId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
