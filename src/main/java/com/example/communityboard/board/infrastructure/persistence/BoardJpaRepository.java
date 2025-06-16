package com.example.communityboard.board.infrastructure.persistence;

import com.example.communityboard.board.domain.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardJpaRepository extends JpaRepository<Board, Long> {

}
