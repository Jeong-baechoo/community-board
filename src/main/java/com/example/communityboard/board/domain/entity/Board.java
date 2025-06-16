package com.example.communityboard.board.domain.entity;

import com.example.communityboard.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "board")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(length = 200)
    private String description;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BoardType boardType;



    private Board(String title, String description, BoardType boardType) {
        validateTitle(title);
        validateDescription(description);
        validateBoardType(boardType);
        this.title = title;
        this.description = description;
        this.boardType = boardType;
    }

    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 공백이 불가합니다.");
        }
        if (title.length() < 2) {  // 최소 길이 추가
            throw new IllegalArgumentException("제목은 2자 이상이어야 합니다.");
        }
        if (title.length() > 50) {
            throw new IllegalArgumentException("제목은 50자를 초과할 수 없습니다.");
        }
    }

    private void validateBoardType(BoardType boardType) {
        if (boardType == null) {
            throw new IllegalArgumentException("게시판 타입은 필수입니다.");
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > 200) {
            throw new IllegalArgumentException("설명은 200자를 초과할 수 없습니다.");
        }
    }


    public void updateBoardInfo(String title, String description) {
        validateTitle(title);
        this.title = title;
        this.description = description;
    }

    public static Board create(String title, String description, BoardType boardType) {
        return new Board(title, description, boardType);
    }

}
