package com.example.communityboard.member.domain.entity;

import com.example.communityboard.common.BaseEntity;
import com.example.communityboard.member.domain.vo.Email;
import com.example.communityboard.member.domain.vo.LoginId;
import com.example.communityboard.member.domain.vo.Nickname;
import com.example.communityboard.member.domain.vo.Password;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private LoginId loginId;

    @Embedded
    private Password password;

    @Embedded
    private Nickname nickname;

    @Embedded
    private Email email;


    private Member(LoginId loginId, Password password, Nickname nickname, Email email) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
    }

    public static Member register(String loginId, String password, String nickname, String email, PasswordEncoder encoder){
        return new Member(
            LoginId.of(loginId),
            Password.ofRaw(password, encoder),
            Nickname.of(nickname),
            Email.of(email)
        );
    }
    
    // DB에서 조회할 때 사용 (이미 암호화된 비밀번호)
    public static Member fromDatabase(Long id, String loginId, String encryptedPassword, String nickname, String email){
        Member member = new Member(
            LoginId.of(loginId),
            Password.ofEncrypted(encryptedPassword),
            Nickname.of(nickname),
            Email.of(email)
        );
        member.id = id;
        return member;
    }

    public void changePassword(String newPassword, PasswordEncoder encoder){
        this.password = Password.ofRaw(newPassword, encoder);
    }

    public void changeNickname(String newNickname){
        this.nickname = Nickname.of(newNickname);
    }

    public void changeEmail(String newEmail) {
        this.email = Email.of(newEmail);
    }

    public boolean matchPassword(String rawPassword, PasswordEncoder encoder) {
        return this.password.match(rawPassword, encoder);
    }

}
