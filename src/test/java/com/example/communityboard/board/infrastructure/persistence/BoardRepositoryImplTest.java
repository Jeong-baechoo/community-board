package com.example.communityboard.board.infrastructure.persistence;

import com.example.communityboard.board.domain.entity.Board;
import com.example.communityboard.board.domain.entity.BoardType;
import com.example.communityboard.board.domain.repository.BoardRepository;
import com.example.communityboard.common.config.JpaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({BoardRepositoryImpl.class, JpaConfig.class})
class BoardRepositoryImplTest {

    @Autowired
    private BoardRepository boardRepository;

    private Board savedBoard;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        Board board = Board.create("테스트 게시판", "테스트 설명", BoardType.FREE);
        savedBoard = boardRepository.save(board);
    }

    @Test
    @DisplayName("게시판을 저장할 수 있다")
    void save() {
        // given
        Board board = Board.create("새로운 게시판", "새로운 설명", BoardType.NOTICE);

        // when
        Board result = boardRepository.save(board);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getTitle()).isEqualTo("새로운 게시판");
        assertThat(result.getDescription()).isEqualTo("새로운 설명");
        assertThat(result.getBoardType()).isEqualTo(BoardType.NOTICE);
        assertThat(result.getCreatedAt()).isNotNull();
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("ID로 게시판을 조회할 수 있다")
    void findById() {
        // when
        Optional<Board> result = boardRepository.findById(savedBoard.getId());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("테스트 게시판");
        assertThat(result.get().getDescription()).isEqualTo("테스트 설명");
        assertThat(result.get().getBoardType()).isEqualTo(BoardType.FREE);
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회시 빈 Optional을 반환한다")
    void findByIdNotFound() {
        // when
        Optional<Board> result = boardRepository.findById(999L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("모든 게시판을 조회할 수 있다")
    void findAll() {
        // given
        Board board2 = Board.create("공지사항", "공지사항 게시판", BoardType.NOTICE);
        Board board3 = Board.create("QnA", "질문답변 게시판", BoardType.QNA);
        boardRepository.save(board2);
        boardRepository.save(board3);

        // when
        List<Board> result = boardRepository.findAll();

        // then
        assertThat(result).hasSize(3);
        assertThat(result).extracting("title")
                .containsExactlyInAnyOrder("테스트 게시판", "공지사항", "QnA");
    }

    @Test
    @DisplayName("게시판 정보를 수정할 수 있다")
    void update() {
        // given
        String newTitle = "수정된 제목";
        String newDescription = "수정된 설명";

        // when
        savedBoard.updateBoardInfo(newTitle, newDescription);
        Board updated = boardRepository.save(savedBoard);

        // then
        assertThat(updated.getTitle()).isEqualTo(newTitle);
        assertThat(updated.getDescription()).isEqualTo(newDescription);
        assertThat(updated.getBoardType()).isEqualTo(BoardType.FREE); // 타입은 변경되지 않음
        assertThat(updated.getUpdatedAt()).isNotNull();
        assertThat(updated.getCreatedAt()).isNotNull();
    }
}