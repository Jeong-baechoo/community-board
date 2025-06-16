package com.example.communityboard.member.domain.repository;

import com.example.communityboard.member.domain.entity.Member;
import com.example.communityboard.member.domain.vo.Email;
import com.example.communityboard.member.domain.vo.LoginId;

import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    
    Optional<Member> findByLoginId(LoginId loginId);
    
    boolean existsByLoginId(LoginId loginId);
    
    boolean existsByEmail(Email email);
}