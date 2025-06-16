package com.example.communityboard.member.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private final Long memberId;
    private final String loginId;
    private final String nickname;
    private final String email;
}