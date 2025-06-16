package com.example.communityboard.board.application.service;

import com.example.communityboard.board.domain.entity.Board;
import com.example.communityboard.board.domain.entity.BoardType;
import com.example.communityboard.board.domain.repository.BoardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;

    private Board testBoard;

    @BeforeEach
    void setUp() {
        testBoard = Board.create("테스트 게시판", "테스트 설명", BoardType.FREE);
    }

    @Test
    @DisplayName("게시판을 생성한다")
    void createBoard() {
        // given
        String title = "자유게시판";
        String description = "자유롭게 글을 작성할 수 있는 게시판";
        BoardType boardType = BoardType.FREE;

        Board savedBoard = mock(Board.class);
        when(savedBoard.getId()).thenReturn(1L);
        when(savedBoard.getTitle()).thenReturn(title);
        when(savedBoard.getDescription()).thenReturn(description);
        when(savedBoard.getBoardType()).thenReturn(boardType);

        when(boardRepository.save(any(Board.class))).thenReturn(savedBoard);

        // when
        Board result = boardService.createBoard(title, description, boardType);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo(title);
        assertThat(result.getDescription()).isEqualTo(description);
        assertThat(result.getBoardType()).isEqualTo(boardType);

        verify(boardRepository).save(any(Board.class));
    }

    @Test
    @DisplayName("ID로 게시판을 조회한다")
    void getBoard() {
        // given
        Long boardId = 1L;
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(testBoard));

        // when
        Board result = boardService.getBoard(boardId);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("테스트 게시판");
        assertThat(result.getDescription()).isEqualTo("테스트 설명");
        assertThat(result.getBoardType()).isEqualTo(BoardType.FREE);

        verify(boardRepository).findById(boardId);
    }

    @Test
    @DisplayName("존재하지 않는 게시판 조회시 예외가 발생한다")
    void getBoardNotFound() {
        // given
        Long boardId = 999L;
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.getBoard(boardId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("게시판을 찾을 수 없습니다. ID: " + boardId);
    }

    @Test
    @DisplayName("모든 게시판 목록을 조회한다")
    void getAllBoards() {
        // given
        Board board1 = Board.create("공지사항", "공지사항 게시판", BoardType.NOTICE);
        Board board2 = Board.create("자유게시판", "자유 게시판", BoardType.FREE);
        List<Board> boards = Arrays.asList(board1, board2);

        when(boardRepository.findAll()).thenReturn(boards);

        // when
        List<Board> result = boardService.getAllBoards();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("공지사항");
        assertThat(result.get(1).getTitle()).isEqualTo("자유게시판");

        verify(boardRepository).findAll();
    }

    @Test
    @DisplayName("게시판 정보를 수정한다")
    void updateBoard() {
        // given
        Long boardId = 1L;
        String newTitle = "수정된 제목";
        String newDescription = "수정된 설명";

        Board existingBoard = mock(Board.class);
        when(boardRepository.findById(boardId)).thenReturn(Optional.of(existingBoard));
        when(boardRepository.save(existingBoard)).thenReturn(existingBoard);

        // when
        Board result = boardService.updateBoard(boardId, newTitle, newDescription);

        // then
        verify(existingBoard).updateBoardInfo(newTitle, newDescription);
        verify(boardRepository).findById(boardId);
        verify(boardRepository).save(existingBoard);
    }

    @Test
    @DisplayName("존재하지 않는 게시판 수정시 예외가 발생한다")
    void updateBoardNotFound() {
        // given
        Long boardId = 999L;
        when(boardRepository.findById(boardId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> boardService.updateBoard(boardId, "새제목", "새설명"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("게시판을 찾을 수 없습니다. ID: " + boardId);
    }
}
