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
public class Nickname {
    
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 10;
    private static final String ALLOWED_PATTERN = "^[가-힣a-zA-Z0-9]+$";

    @Column(name = "nickname", nullable = false, unique = true)
    private String value;

    private Nickname(String value) {
        validate(value);
        this.value = value;
    }

    public static Nickname of(String value) {
        return new Nickname(value);
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("닉네임은 필수입니다.");
        }
        if (value.length() < MIN_LENGTH || value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                String.format("닉네임은 %d자 이상 %d자 이하여야 합니다.", MIN_LENGTH, MAX_LENGTH)
            );
        }
        if (!value.matches(ALLOWED_PATTERN)) {
            throw new IllegalArgumentException("닉네임은 한글, 영문, 숫자만 사용 가능합니다.");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Nickname nickname = (Nickname) o;
        return Objects.equals(value, nickname.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
