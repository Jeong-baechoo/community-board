package com.example.communityboard.board.application.dto.response;

import com.example.communityboard.board.domain.entity.Board;
import com.example.communityboard.board.domain.entity.BoardType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final BoardType boardType;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private BoardResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.description = board.getDescription();
        this.boardType = board.getBoardType();
        this.createdAt = board.getCreatedAt();
        this.updatedAt = board.getUpdatedAt();
    }

    public static BoardResponse from(Board board) {
        return new BoardResponse(board);
    }
}