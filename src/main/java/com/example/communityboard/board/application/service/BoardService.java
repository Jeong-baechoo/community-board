package com.example.communityboard.board.application.service;

import com.example.communityboard.board.domain.entity.Board;
import com.example.communityboard.board.domain.entity.BoardType;
import com.example.communityboard.board.domain.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    public Board createBoard(String title, String description, BoardType boardType) {
        Board board = Board.create(title, description, boardType);
        return boardRepository.save(board);
    }

    public Board getBoard(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시판을 찾을 수 없습니다. ID: " + boardId));
    }

    public List<Board> getAllBoards() {
        return boardRepository.findAll();
    }

    @Transactional
    public Board updateBoard(Long boardId, String title, String description) {
        Board board = getBoard(boardId);
        board.updateBoardInfo(title, description);
        return boardRepository.save(board);
    }
}
