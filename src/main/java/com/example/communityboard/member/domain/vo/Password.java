package com.example.communityboard.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    @Column(name = "password", nullable = false)
    private String value;

    private Password(String value) {
        validate(value);
        this.value = value;
    }

    public static Password of(String value) {
        return new Password(value);
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }
        if (value.length() < 8) {
            throw new IllegalArgumentException("비밀번호는 최소 8자 이상이어야 합니다.");
        }

        int typeCount = 0;
        if (value.matches(".*[a-zA-Z].*")) typeCount++;
        if (value.matches(".*[0-9].*")) typeCount++;
        if (value.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':.,<>?/].*")) typeCount++;

        if (typeCount < 2) {
            throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자 중 2종류 이상 포함해야 합니다.");
        }
    }

    public boolean matches(String rawPassword) {
        return this.value.equals(rawPassword);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return Objects.equals(value, password.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
