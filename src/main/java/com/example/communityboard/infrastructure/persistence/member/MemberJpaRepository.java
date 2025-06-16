package com.example.communityboard.infrastructure.persistence.member;

import com.example.communityboard.domain.member.entity.Member;
import com.example.communityboard.domain.member.vo.Email;
import com.example.communityboard.domain.member.vo.LoginId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberJpaRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(LoginId loginId);
    
    boolean existsByLoginId(LoginId loginId);
    
    boolean existsByEmail(Email email);
}