package com.example.communityboard.member.infrastructure.persistence;

import com.example.communityboard.member.domain.entity.Member;
import com.example.communityboard.member.domain.repository.MemberRepository;
import com.example.communityboard.member.domain.vo.Email;
import com.example.communityboard.member.domain.vo.LoginId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(MemberRepositoryImpl.class)
class MemberRepositoryImplTest {

    @Autowired
    private MemberRepository memberRepository;

    private Member savedMember;

    @BeforeEach
    void setUp() {
        Member member = Member.register(
                "testuser",
                "password123!",
                "테스트유저",
                "test@example.com"
        );
        savedMember = memberRepository.save(member);
    }

    @Test
    @DisplayName("회원을 저장할 수 있다")
    void save() {
        // given
        Member newMember = Member.register(
                "newuser",
                "newpass123!",
                "새유저",
                "new@example.com"
        );

        // when
        Member saved = memberRepository.save(newMember);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getLoginId().getValue()).isEqualTo("newuser");
    }

    @Test
    @DisplayName("로그인 ID로 회원을 조회할 수 있다")
    void findByLoginId() {
        // given
        LoginId loginId = LoginId.of("testuser");

        // when
        Optional<Member> found = memberRepository.findByLoginId(loginId);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getLoginId().getValue()).isEqualTo("testuser");
        assertThat(found.get().getEmail().getValue()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("존재하지 않는 로그인 ID로 조회시 빈 Optional을 반환한다")
    void findByLoginId_NotFound() {
        // given
        LoginId loginId = LoginId.of("nonexistent");

        // when
        Optional<Member> found = memberRepository.findByLoginId(loginId);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("로그인 ID 중복 여부를 확인할 수 있다")
    void existsByLoginId() {
        // given
        LoginId existingLoginId = LoginId.of("testuser");
        LoginId newLoginId = LoginId.of("newuser");

        // when & then
        assertThat(memberRepository.existsByLoginId(existingLoginId)).isTrue();
        assertThat(memberRepository.existsByLoginId(newLoginId)).isFalse();
    }

    @Test
    @DisplayName("이메일 중복 여부를 확인할 수 있다")
    void existsByEmail() {
        // given
        Email existingEmail = Email.of("test@example.com");
        Email newEmail = Email.of("new@example.com");

        // when & then
        assertThat(memberRepository.existsByEmail(existingEmail)).isTrue();
        assertThat(memberRepository.existsByEmail(newEmail)).isFalse();
    }
}