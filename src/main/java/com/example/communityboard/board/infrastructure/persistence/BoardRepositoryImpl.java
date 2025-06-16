package com.example.communityboard.board.infrastructure.persistence;

import com.example.communityboard.board.domain.entity.Board;
import com.example.communityboard.board.domain.entity.BoardType;
import com.example.communityboard.board.domain.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class BoardRepositoryImpl implements BoardRepository {

    private final BoardJpaRepository boardJpaRepository;

    @Override
    public Board save(Board board) {
        return boardJpaRepository.save(board);
    }

    @Override
    public Optional<Board> findById(Long id) {
        return boardJpaRepository.findById(id);
    }

    @Override
    public List<Board> findAll() {
        return boardJpaRepository.findAll();
    }

}
