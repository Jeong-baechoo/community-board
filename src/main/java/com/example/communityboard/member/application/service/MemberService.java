package com.example.communityboard.member.application.service;

import com.example.communityboard.member.application.dto.LoginRequest;
import com.example.communityboard.member.application.dto.LoginResponse;
import com.example.communityboard.member.application.dto.SignupRequest;
import com.example.communityboard.member.application.dto.SignupResponse;
import com.example.communityboard.member.application.exception.DuplicateEmailException;
import com.example.communityboard.member.application.exception.DuplicateLoginIdException;
import com.example.communityboard.member.application.exception.InvalidLoginException;
import com.example.communityboard.member.domain.entity.Member;
import com.example.communityboard.member.domain.repository.MemberRepository;
import com.example.communityboard.member.domain.vo.Email;
import com.example.communityboard.member.domain.vo.LoginId;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        LoginId loginId = LoginId.of(request.getLoginId());
        
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(InvalidLoginException::new);
        
        if (!member.matchPassword(request.getPassword(), passwordEncoder)) {
            throw new InvalidLoginException();
        }
        
        return new LoginResponse(
                member.getId(),
                member.getLoginId().getValue(),
                member.getNickname().getValue(),
                member.getEmail().getValue()
        );
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 중복 검증
        LoginId loginId = LoginId.of(request.getLoginId());
        if (memberRepository.existsByLoginId(loginId)) {
            throw new DuplicateLoginIdException();
        }
        
        Email email = Email.of(request.getEmail());
        if (memberRepository.existsByEmail(email)) {
            throw new DuplicateEmailException();
        }
        
        // 회원 생성 및 저장
        Member member = Member.register(
                request.getLoginId(),
                request.getPassword(),
                request.getNickname(),
                request.getEmail(),
                passwordEncoder
        );
        
        Member savedMember = memberRepository.save(member);
        
        return new SignupResponse(
                savedMember.getId(),
                savedMember.getLoginId().getValue(),
                savedMember.getNickname().getValue(),
                savedMember.getEmail().getValue()
        );
    }
}
