package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.Board;
import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.domain.User;
import com.sideproject.codemoim.exception.BoardNotFoundException;
import com.sideproject.codemoim.exception.ProfileNotFoundException;
import com.sideproject.codemoim.repository.BoardRepository;
import com.sideproject.codemoim.repository.ProfileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @InjectMocks
    BoardService boardService;

    @Mock
    BoardRepository boardRepository;

    @Mock
    ProfileRepository profileRepository;

    @Test
    @DisplayName("상위 게시판 및 하위 게시판 조회 성공 테스트")
    void searchTreeListTest_success() {
        List<Board> boardList = new ArrayList<>();

        given(boardRepository.searchTreeList("all")).willReturn(boardList);

        Assertions.assertDoesNotThrow(() -> {
            boardService.searchTreeList("all");
        });
    }

    @Test
    @DisplayName("상위 게시판 및 하위 게시판 조회 실패 테스트")
    void searchTreeListTest_fail() {
        given(boardRepository.searchTreeList("all")).willReturn(null);

        Assertions.assertThrows(NullPointerException.class, () -> {
            boardService.searchTreeList("all");
        });
    }

    @Test
    @DisplayName("게시판 정보 조회 성공 테스트")
    void searchBoardInfoTest_success() {
        Board board = mock(Board.class);

        given(boardRepository.findById(anyLong())).willReturn(Optional.ofNullable(board));

        Assertions.assertDoesNotThrow(() -> {
            boardService.searchBoardInfo(anyLong());
        });
    }

    @Test
    @DisplayName("게시판 정보 조회 실패 테스트")
    void searchBoardInfoTest_fail() {
        given(boardRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        Assertions.assertThrows(BoardNotFoundException.class, () -> {
            boardService.searchBoardInfo(anyLong());
        });
    }

    @Test
    @DisplayName("게시판 삭제 가능여부 체크 성공 테스트")
    void searchBoardDeletableCheck_success() {
        given(boardRepository.subBoardExist(anyLong())).willReturn(false);
        given(boardRepository.postExist(anyLong())).willReturn(false);

        Assertions.assertDoesNotThrow(() -> {
            boardService.searchBoardDeletableCheck(anyLong());
        });
    }

    @Test
    @DisplayName("전체 게시판 조회 성공 테스트")
    void searchAllListTest_success() {
        List<Board> boardList = new ArrayList<>();

        given(boardRepository.findAllByStatus(true)).willReturn(boardList);

        Assertions.assertDoesNotThrow(() -> {
            boardService.searchAllList();
        });
    }

    @Test
    @DisplayName("전체 게시판 조회 실패 테스트")
    void searchAllListTest_fail() {
        given(boardRepository.findAllByStatus(true)).willReturn(null);

        Assertions.assertThrows(NullPointerException.class, () -> {
            boardService.searchAllList();
        });
    }

    @Test
    @DisplayName("게시판 저장 성공 테스트")
    void saveBoardTest_success() {
        Map<String, Object> boardInfo = new HashMap<>();

        boardInfo.put("profileId", 1);
        boardInfo.put("parent", 1);
        boardInfo.put("name", "test");
        boardInfo.put("status", 1);
        boardInfo.put("url", "/test");
        boardInfo.put("icon", "test");
        boardInfo.put("sort", 0);
        boardInfo.put("display", 1);
        boardInfo.put("sortDisplay", 0);

        Board board = Board.builder().build();

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);
        given(boardRepository.searchByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(board));

        Assertions.assertDoesNotThrow(() -> {
            boardService.createBoard(boardInfo, anyLong());
        });
    }

    @Test
    @DisplayName("게시판 저장 실패 테스트")
    void saveBoardTest_fail() {
        Map<String, Object> boardInfo = new HashMap<>();

        boardInfo.put("profileId", 1);
        boardInfo.put("parent", 1);
        boardInfo.put("name", "test");
        boardInfo.put("status", 1);
        boardInfo.put("url", "/test");
        boardInfo.put("icon", "test");
        boardInfo.put("sort", "0");

        given(boardRepository.searchByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(null));

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            boardService.createBoard(boardInfo, anyLong());
        });

        Assertions.assertThrows(BoardNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            boardService.createBoard(boardInfo, anyLong());
        });
    }

    @Test
    @DisplayName("게시판 수정 성공 테스트")
    void modifyBoardTest_success() {
        Map<String, Object> boardInfo = new HashMap<>();

        boardInfo.put("id", 1);
        boardInfo.put("profileId", 1);
        boardInfo.put("parent", 2);
        boardInfo.put("name", "test");
        boardInfo.put("status", 1);
        boardInfo.put("url", "/test");
        boardInfo.put("icon", "test");
        boardInfo.put("sort", 0);
        boardInfo.put("display", 0);

        Board board = Board.builder()
                .display(false)
                .build();

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);
        given(boardRepository.findById(anyLong())).willReturn(Optional.ofNullable(board));

        Assertions.assertDoesNotThrow(() -> {
            boardService.modifyBoard(boardInfo, anyLong());
        });
    }

    @Test
    @DisplayName("게시판 수정 실패 테스트")
    void modifyBoardTest_fail() {
        Map<String, Object> boardInfo = new HashMap<>();

        boardInfo.put("id", 1);
        boardInfo.put("profileId", 1);
        boardInfo.put("parent", 2);
        boardInfo.put("name", "test");
        boardInfo.put("status", 1);
        boardInfo.put("url", "/test");
        boardInfo.put("icon", "test");
        boardInfo.put("sort", 0);
        boardInfo.put("display", 0);

        given(boardRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            boardService.modifyBoard(boardInfo, anyLong());
        });

        Assertions.assertThrows(BoardNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            boardService.modifyBoard(boardInfo, anyLong());
        });
    }

    @Test
    @DisplayName("게시판 삭제 성공 테스트")
    void deleteBoardTest_success() {
        Map<String, Object> boardInfo = new HashMap<>();

        boardInfo.put("id", 1);
        boardInfo.put("profileId", 1);

        Board board = Board.builder().build();

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        given(boardRepository.subBoardExist(anyLong())).willReturn(false);
        given(boardRepository.postExist(anyLong())).willReturn(false);
        given(boardRepository.findById(anyLong())).willReturn(Optional.ofNullable(board));

        Assertions.assertDoesNotThrow(() -> {
            boardService.deleteBoard(boardInfo, anyLong());
        });
    }

    @Test
    @DisplayName("게시판 삭제 실패 테스트")
    void deleteBoardTest_fail() {
        Map<String, Object> boardInfo = new HashMap<>();

        boardInfo.put("id", 1);
        boardInfo.put("profileId", 1);

        given(boardRepository.subBoardExist(anyLong())).willReturn(false);
        given(boardRepository.postExist(anyLong())).willReturn(false);
        given(boardRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            boardService.deleteBoard(boardInfo, anyLong());
        });

        Assertions.assertThrows(BoardNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            boardService.deleteBoard(boardInfo, anyLong());
        });
    }
    
    @Test
    @DisplayName("하위 게시판 조회 성공 테스트")
    void searchSubListTest_success() {
        Board board = mock(Board.class);

        List<Board> boardList = new ArrayList<>();
        boardList.add(board);

        given(boardRepository.searchSubList()).willReturn(boardList);

        Assertions.assertDoesNotThrow(() -> {
            boardService.searchSubList();
        });
    }

}