package com.example.communityboard.board.integration;

import com.example.communityboard.board.application.dto.request.CreateBoardRequest;
import com.example.communityboard.board.application.dto.request.UpdateBoardRequest;
import com.example.communityboard.board.domain.entity.Board;
import com.example.communityboard.board.domain.entity.BoardType;
import com.example.communityboard.board.domain.repository.BoardRepository;
import com.example.communityboard.member.domain.entity.Member;
import com.example.communityboard.member.domain.repository.MemberRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BoardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member adminMember;
    private Member normalMember;

    @BeforeEach
    void setUp() {
        // 관리자 회원 생성
        adminMember = Member.registerAdmin("admin", "admin123!", "관리자", "admin@test.com", passwordEncoder);
        memberRepository.save(adminMember);

        // 일반 회원 생성
        normalMember = Member.registerMember("user", "user123!", "일반사용자", "user@test.com", passwordEncoder);
        memberRepository.save(normalMember);
    }

    @Test
    @DisplayName("관리자가 게시판을 생성하고 조회한다")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createAndGetBoard() throws Exception {
        // given
        CreateBoardRequest request = new CreateBoardRequest("통합테스트 게시판", "통합테스트용 게시판입니다", BoardType.FREE);

        // when - 게시판 생성
        String response = mockMvc.perform(post("/api/boards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("게시판이 생성되었습니다."))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then - DB 확인
        assertThat(boardRepository.findAll()).hasSize(1);
        Board savedBoard = boardRepository.findAll().get(0);
        assertThat(savedBoard.getTitle()).isEqualTo("통합테스트 게시판");
        assertThat(savedBoard.getDescription()).isEqualTo("통합테스트용 게시판입니다");
        assertThat(savedBoard.getBoardType()).isEqualTo(BoardType.FREE);

        // when - 게시판 조회
        mockMvc.perform(get("/api/boards/{boardId}", savedBoard.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(savedBoard.getId()))
                .andExpect(jsonPath("$.data.title").value("통합테스트 게시판"));
    }

    @Test
    @DisplayName("게시판 생성, 수정, 목록 조회 전체 플로우")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void boardFullFlow() throws Exception {
        // 1. 게시판 생성
        CreateBoardRequest createRequest1 = new CreateBoardRequest("공지사항", "공지사항 게시판", BoardType.NOTICE);
        CreateBoardRequest createRequest2 = new CreateBoardRequest("자유게시판", "자유게시판", BoardType.FREE);

        mockMvc.perform(post("/api/boards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/boards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest2)))
                .andExpect(status().isCreated());

        // 2. 목록 조회
        mockMvc.perform(get("/api/boards"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].title").exists())
                .andExpect(jsonPath("$.data[1].title").exists());

        // 3. 게시판 수정
        Board board = boardRepository.findAll().get(0);
        UpdateBoardRequest updateRequest = new UpdateBoardRequest("수정된 공지사항", "수정된 설명입니다");

        mockMvc.perform(put("/api/boards/{boardId}", board.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("수정된 공지사항"))
                .andExpect(jsonPath("$.message").value("게시판 설정이 변경되었습니다."));

        // 4. DB 확인
        Board updatedBoard = boardRepository.findById(board.getId()).orElseThrow();
        assertThat(updatedBoard.getTitle()).isEqualTo("수정된 공지사항");
        assertThat(updatedBoard.getDescription()).isEqualTo("수정된 설명입니다");
    }

    @Test
    @DisplayName("일반 사용자는 게시판을 생성할 수 없다")
    @WithMockUser(username = "user", roles = "MEMBER")
    void normalUserCannotCreateBoard() throws Exception {
        // given
        CreateBoardRequest request = new CreateBoardRequest("불가능한 게시판", "생성 불가", BoardType.FREE);

        // when & then
        mockMvc.perform(post("/api/boards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())  // 에러 로그 출력
                .andExpect(status().isForbidden());

        // DB 확인
        assertThat(boardRepository.findAll()).isEmpty();
    }

    @Test
    @DisplayName("유효하지 않은 데이터로 게시판 생성 실패")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void createBoardWithInvalidData() throws Exception {
        // given - 제목이 너무 짧음
        CreateBoardRequest request = new CreateBoardRequest("a", "설명", BoardType.FREE);

        // when & then
        mockMvc.perform(post("/api/boards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest());

        // DB 확인
        assertThat(boardRepository.findAll()).isEmpty();
    }
}