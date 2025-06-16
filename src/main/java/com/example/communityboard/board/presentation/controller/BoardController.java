package com.example.communityboard.board.presentation.controller;

import com.example.communityboard.board.application.dto.request.CreateBoardRequest;
import com.example.communityboard.board.application.dto.request.UpdateBoardRequest;
import com.example.communityboard.board.application.dto.response.BoardResponse;
import com.example.communityboard.board.application.service.BoardService;
import com.example.communityboard.board.domain.entity.Board;
import com.example.communityboard.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") //운영자만 게시판 생성 가능
    public ResponseEntity<ApiResponse<BoardResponse>> createBoard(@Valid @RequestBody CreateBoardRequest request) {
        Board board = boardService.createBoard(request.getTitle(), request.getDescription(), request.getBoardType());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(BoardResponse.from(board), "게시판이 생성되었습니다."));
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<ApiResponse<BoardResponse>> getBoard(@PathVariable Long boardId) {
        Board board = boardService.getBoard(boardId);
        return ResponseEntity.ok(ApiResponse.success(BoardResponse.from(board)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BoardResponse>>> getAllBoards() {
        List<Board> boards = boardService.getAllBoards();
        List<BoardResponse> responses = boards.stream()
                .map(BoardResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PutMapping("/{boardId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<BoardResponse>> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody UpdateBoardRequest request) {
        Board board = boardService.updateBoard(boardId, request.getTitle(), request.getDescription());
        return ResponseEntity.ok(ApiResponse.success(BoardResponse.from(board), "게시판 설정이 변경되었습니다."));
    }
}
