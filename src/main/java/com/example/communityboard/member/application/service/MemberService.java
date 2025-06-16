package com.example.communityboard.member.application.service;

import com.example.communityboard.member.application.dto.LoginRequest;
import com.example.communityboard.member.application.dto.LoginResponse;
import com.example.communityboard.member.application.exception.InvalidLoginException;
import com.example.communityboard.member.domain.entity.Member;
import com.example.communityboard.member.domain.repository.MemberRepository;
import com.example.communityboard.member.domain.vo.LoginId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public LoginResponse login(LoginRequest request) {
        LoginId loginId = LoginId.of(request.getLoginId());
        
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(InvalidLoginException::new);
        
        if (!member.matchPassword(request.getPassword())) {
            throw new InvalidLoginException();
        }
        
        return new LoginResponse(
                member.getId(),
                member.getLoginId().getValue(),
                member.getNickname().getValue(),
                member.getEmail().getValue()
        );
    }

}
