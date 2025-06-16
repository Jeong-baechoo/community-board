package com.example.communityboard.infrastructure.persistence.member;

import com.example.communityboard.domain.member.entity.Member;
import com.example.communityboard.domain.member.repository.MemberRepository;
import com.example.communityboard.domain.member.vo.Email;
import com.example.communityboard.domain.member.vo.LoginId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    
    private final MemberJpaRepository memberJpaRepository;
    
    @Override
    public Member save(Member member) {
        return memberJpaRepository.save(member);
    }
    
    @Override
    public Optional<Member> findByLoginId(LoginId loginId) {
        return memberJpaRepository.findByLoginId(loginId);
    }
    
    @Override
    public boolean existsByLoginId(LoginId loginId) {
        return memberJpaRepository.existsByLoginId(loginId);
    }
    
    @Override
    public boolean existsByEmail(Email email) {
        return memberJpaRepository.existsByEmail(email);
    }
}