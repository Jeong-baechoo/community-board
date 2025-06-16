package com.example.communityboard.member.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "로그인 ID는 필수입니다")
    private String loginId;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}