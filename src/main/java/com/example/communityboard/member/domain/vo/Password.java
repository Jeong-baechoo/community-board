package com.example.communityboard.member.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    @Column(name = "password", nullable = false)
    private String value;
    
    private static final String BCRYPT_PREFIX = "$2a$";
    private static final String BCRYPT_PREFIX_2 = "$2b$";
    private static final String BCRYPT_PREFIX_3 = "$2y$";

    private Password(String value) {
        this.value = value;
    }
    
    private Password(String rawPassword, PasswordEncoder encoder) {
        validate(rawPassword);
        this.value = encoder.encode(rawPassword);
    }

    // 암호화된 비밀번호로 생성 (DB에서 조회할 때)
    public static Password ofEncrypted(String encryptedValue) {
        return new Password(encryptedValue);
    }
    
    // 평문 비밀번호로 생성 (회원가입, 비밀번호 변경 시)
    public static Password ofRaw(String rawPassword, PasswordEncoder encoder) {
        return new Password(rawPassword, encoder);
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

    public boolean match(String rawPassword, PasswordEncoder encoder) {
        if (rawPassword == null) {
            return false;
        }
        return encoder.matches(rawPassword, this.value);
    }
    
    // 암호화되어 있는지 확인
    public boolean isEncrypted() {
        return value != null && (value.startsWith(BCRYPT_PREFIX) || 
               value.startsWith(BCRYPT_PREFIX_2) || 
               value.startsWith(BCRYPT_PREFIX_3));
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
