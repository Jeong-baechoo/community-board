package com.example.communityboard.member.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequest {
    @NotBlank(message = "로그인 ID는 필수입니다")
    @Size(min = 4, max = 20, message = "로그인 ID는 4자 이상 20자 이하여야 합니다")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "로그인 ID는 영문자와 숫자만 사용 가능합니다")
    private String loginId;
    
    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).*$", 
             message = "비밀번호는 대소문자, 숫자, 특수문자를 포함해야 합니다")
    private String password;
    
    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하여야 합니다")
    private String nickname;
    
    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    private String email;
}