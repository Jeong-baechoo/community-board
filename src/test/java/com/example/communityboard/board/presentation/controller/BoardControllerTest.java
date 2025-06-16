package com.example.communityboard.board.presentation.controller;

import com.example.communityboard.board.application.dto.request.CreateBoardRequest;
import com.example.communityboard.board.application.dto.request.UpdateBoardRequest;
import com.example.communityboard.board.application.service.BoardService;
import com.example.communityboard.board.domain.entity.Board;
import com.example.communityboard.board.domain.entity.BoardType;
import com.example.communityboard.common.config.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
@Import(SecurityConfig.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BoardService boardService;

    private Board testBoard;

    @BeforeEach
    void setUp() {
        testBoard = Board.create("테스트 게시판", "테스트 설명", BoardType.FREE);
    }

    @Test
    @DisplayName("관리자가 게시판을 생성한다")
    @WithMockUser(roles = "ADMIN")
    void createBoard() throws Exception {
        // given
        CreateBoardRequest request = new CreateBoardRequest("자유게시판", "자유롭게 글을 작성하는 게시판", BoardType.FREE);

        Board createdBoard = Board.create(request.getTitle(), request.getDescription(), request.getBoardType());
        when(boardService.createBoard(anyString(), anyString(), any(BoardType.class))).thenReturn(createdBoard);

        // when & then
        mockMvc.perform(post("/api/boards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("자유게시판"))
                .andExpect(jsonPath("$.data.description").value("자유롭게 글을 작성하는 게시판"))
                .andExpect(jsonPath("$.data.boardType").value("FREE"))
                .andExpect(jsonPath("$.message").value("게시판이 생성되었습니다."));
    }

    @Test
    @DisplayName("일반 사용자가 게시판 생성 시도시 접근 거부")
    @WithMockUser(roles = "MEMBER")
    void createBoardAccessDenied() throws Exception {
        // given
        CreateBoardRequest request = new CreateBoardRequest("자유게시판", "설명", BoardType.FREE);

        // when & then
        mockMvc.perform(post("/api/boards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("게시판 생성시 유효성 검증 실패")
    @WithMockUser(roles = "ADMIN")
    void createBoardValidationFail() throws Exception {
        // given
        CreateBoardRequest request = new CreateBoardRequest("", "설명", BoardType.FREE);

        // when & then
        mockMvc.perform(post("/api/boards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게시판 단건 조회")
    @WithMockUser
    void getBoard() throws Exception {
        // given
        Long boardId = 1L;
        when(boardService.getBoard(boardId)).thenReturn(testBoard);

        // when & then
        mockMvc.perform(get("/api/boards/{boardId}", boardId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("테스트 게시판"))
                .andExpect(jsonPath("$.data.description").value("테스트 설명"))
                .andExpect(jsonPath("$.data.boardType").value("FREE"));
    }

    @Test
    @DisplayName("게시판 목록 조회")
    @WithMockUser
    void getAllBoards() throws Exception {
        // given
        Board board1 = Board.create("공지사항", "공지 게시판", BoardType.NOTICE);
        Board board2 = Board.create("자유게시판", "자유 게시판", BoardType.FREE);
        List<Board> boards = Arrays.asList(board1, board2);

        when(boardService.getAllBoards()).thenReturn(boards);

        // when & then
        mockMvc.perform(get("/api/boards"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].title").value("공지사항"))
                .andExpect(jsonPath("$.data[1].title").value("자유게시판"));
    }

    @Test
    @DisplayName("관리자가 게시판 정보를 수정한다")
    @WithMockUser(roles = "ADMIN")
    void updateBoard() throws Exception {
        // given
        Long boardId = 1L;
        UpdateBoardRequest request = new UpdateBoardRequest("수정된 제목", "수정된 설명");

        Board updatedBoard = Board.create("수정된 제목", "수정된 설명", BoardType.FREE);
        when(boardService.updateBoard(eq(boardId), anyString(), anyString())).thenReturn(updatedBoard);

        // when & then
        mockMvc.perform(put("/api/boards/{boardId}", boardId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("수정된 제목"))
                .andExpect(jsonPath("$.data.description").value("수정된 설명"))
                .andExpect(jsonPath("$.message").value("게시판 설정이 변경되었습니다."));
    }

    @Test
    @DisplayName("일반 사용자가 게시판 수정 시도시 접근 거부")
    @WithMockUser(roles = "MEMBER")
    void updateBoardAccessDenied() throws Exception {
        // given
        Long boardId = 1L;
        UpdateBoardRequest request = new UpdateBoardRequest("수정된 제목", "수정된 설명");

        // when & then
        mockMvc.perform(put("/api/boards/{boardId}", boardId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
