package com.example.communityboard.domain.member.entity;

import com.example.communityboard.domain.common.BaseEntity;
import com.example.communityboard.domain.member.vo.Email;
import com.example.communityboard.domain.member.vo.LoginId;
import com.example.communityboard.domain.member.vo.Nickname;
import com.example.communityboard.domain.member.vo.Password;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
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


    private Member(String loginId, String password, String nickname, String email) {
        this.loginId = LoginId.of(loginId);
        this.password = Password.of(password);
        this.nickname = Nickname.of(nickname);
        this.email = Email.of(email);
    }

    public static Member register(String loginId, String password, String nickname, String email){
        return new Member(loginId, password, nickname, email);
    }

    public void changePassword(String newPassword){
        this.password = Password.of(newPassword);
    }

    public void changeNickname(String newNickname){
        this.nickname = Nickname.of(newNickname);
    }

    public void changeEmail(String newEmail) {
        this.email = Email.of(newEmail);
    }

}
