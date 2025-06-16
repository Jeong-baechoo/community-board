package com.example.communityboard.member.infrastructure.persistence;

import com.example.communityboard.member.domain.entity.Member;
import com.example.communityboard.member.domain.repository.MemberRepository;
import com.example.communityboard.member.domain.vo.Email;
import com.example.communityboard.member.domain.vo.LoginId;
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