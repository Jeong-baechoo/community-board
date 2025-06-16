package com.example.communityboard.member.presentation.controller;

import com.example.communityboard.common.dto.ApiResponse;
import com.example.communityboard.member.application.dto.LoginRequest;
import com.example.communityboard.member.application.dto.LoginResponse;
import com.example.communityboard.member.application.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse response = memberService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "로그인에 성공했습니다."));
    }
}
