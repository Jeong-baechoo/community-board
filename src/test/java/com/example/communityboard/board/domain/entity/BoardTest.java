package com.example.communityboard.board.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

class BoardTest {

    @Test
    @DisplayName("정상적인 게시판 생성")
    void createBoard() {
        // given
        String title = "자유게시판";
        String description = "자유롭게 글을 작성할 수 있는 게시판";
        BoardType boardType = BoardType.FREE;

        // when
        Board board = Board.create(title, description, boardType);

        // then
        assertThat(board).isNotNull();
        assertThat(board.getTitle()).isEqualTo(title);
        assertThat(board.getDescription()).isEqualTo(description);
        assertThat(board.getBoardType()).isEqualTo(boardType);
    }

    @Test
    @DisplayName("설명 없이 게시판 생성")
    void createBoardWithoutDescription() {
        // given
        String title = "공지사항";
        BoardType boardType = BoardType.NOTICE;

        // when
        Board board = Board.create(title, null, boardType);

        // then
        assertThat(board.getTitle()).isEqualTo(title);
        assertThat(board.getDescription()).isNull();
        assertThat(board.getBoardType()).isEqualTo(boardType);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  "})
    @DisplayName("제목이 null이거나 공백일 때 예외 발생")
    void createBoardWithInvalidTitle(String invalidTitle) {
        // when & then
        assertThatThrownBy(() -> Board.create(invalidTitle, "설명", BoardType.FREE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제목은 공백이 불가합니다.");
    }

    @Test
    @DisplayName("제목이 2자 미만일 때 예외 발생")
    void createBoardWithTooShortTitle() {
        // when & then
        assertThatThrownBy(() -> Board.create("가", "설명", BoardType.FREE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제목은 2자 이상이어야 합니다.");
    }

    @Test
    @DisplayName("제목이 50자 초과일 때 예외 발생")
    void createBoardWithTooLongTitle() {
        // given
        String longTitle = "가".repeat(51);

        // when & then
        assertThatThrownBy(() -> Board.create(longTitle, "설명", BoardType.FREE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("제목은 50자를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("설명이 200자 초과일 때 예외 발생")
    void createBoardWithTooLongDescription() {
        // given
        String longDescription = "가".repeat(201);

        // when & then
        assertThatThrownBy(() -> Board.create("게시판", longDescription, BoardType.FREE))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("설명은 200자를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("게시판 타입이 null일 때 예외 발생")
    void createBoardWithNullBoardType() {
        // when & then
        assertThatThrownBy(() -> Board.create("게시판", "설명", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("게시판 타입은 필수입니다.");
    }

    @Test
    @DisplayName("게시판 정보 수정")
    void updateBoardInfo() {
        // given
        Board board = Board.create("원래제목", "원래설명", BoardType.FREE);
        String newTitle = "수정된제목";
        String newDescription = "수정된설명";

        // when
        board.updateBoardInfo(newTitle, newDescription);

        // then
        assertThat(board.getTitle()).isEqualTo(newTitle);
        assertThat(board.getDescription()).isEqualTo(newDescription);
        assertThat(board.getBoardType()).isEqualTo(BoardType.FREE); // 타입은 변경되지 않음
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "가"})
    @DisplayName("수정 시 유효하지 않은 제목으로 예외 발생")
    void updateBoardInfoWithInvalidTitle(String invalidTitle) {
        // given
        Board board = Board.create("원래제목", "원래설명", BoardType.FREE);

        // when & then
        assertThatThrownBy(() -> board.updateBoardInfo(invalidTitle, "새설명"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("모든 게시판 타입으로 생성 가능")
    void createBoardWithAllTypes() {
        for (BoardType type : BoardType.values()) {
            // when
            Board board = Board.create("테스트게시판", "설명", type);

            // then
            assertThat(board.getBoardType()).isEqualTo(type);
        }
    }
}