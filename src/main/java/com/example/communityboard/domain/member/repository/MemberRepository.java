package com.example.communityboard.domain.member.repository;

import com.example.communityboard.domain.member.entity.Member;
import com.example.communityboard.domain.member.vo.Email;
import com.example.communityboard.domain.member.vo.LoginId;

import java.util.Optional;

public interface MemberRepository {
    Member save(Member member);
    
    Optional<Member> findByLoginId(LoginId loginId);
    
    boolean existsByLoginId(LoginId loginId);
    
    boolean existsByEmail(Email email);
}