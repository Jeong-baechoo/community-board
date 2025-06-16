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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    private Member(LoginId loginId, Password password, Nickname nickname, Email email, Role role) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.role = role;
    }

    public static Member registerMember(String loginId, String password, String nickname, String email, PasswordEncoder encoder){
        return new Member(
            LoginId.of(loginId),
            Password.ofRaw(password, encoder),
            Nickname.of(nickname),
            Email.of(email),
            Role.MEMBER
        );
    }

    public static Member registerAdmin(String loginId, String password, String nickname, String email, PasswordEncoder encoder){
        return new Member(
            LoginId.of(loginId),
            Password.ofRaw(password, encoder),
            Nickname.of(nickname),
            Email.of(email),
            Role.ADMIN
        );
    }

    public boolean isAdmin() {
        return this.role == Role.ADMIN;
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
