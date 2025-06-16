package com.example.communityboard.board.domain.repository;

import com.example.communityboard.board.domain.entity.Board;

import java.util.List;
import java.util.Optional;

public interface BoardRepository {
    
    Board save(Board board);
    
    Optional<Board> findById(Long id);
    
    List<Board> findAll();
}
