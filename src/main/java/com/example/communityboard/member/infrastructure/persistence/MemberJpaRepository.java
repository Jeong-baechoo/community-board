package com.example.communityboard.member.infrastructure.persistence;

import com.example.communityboard.member.domain.entity.Member;
import com.example.communityboard.member.domain.vo.Email;
import com.example.communityboard.member.domain.vo.LoginId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(LoginId loginId);
    
    boolean existsByLoginId(LoginId loginId);
    
    boolean existsByEmail(Email email);
}