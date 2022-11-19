package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.config.TestConfig;
import com.sideproject.codemoim.domain.Board;
import com.sideproject.codemoim.domain.Post;
import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

@DataJpaTest
@Import({TestConfig.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BoardRepositoryTest {

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    PostRepository postRepository;

    @Test
    @DisplayName("게시판 아이디와 상태값을 사용한 게시판 조회 테스트")
    void searchByIdAndStatusTest() {
        Board board = Board.builder()
                .name("test")
                .status(true)
                .url("/test")
                .icon("test")
                .sort(0)
                .display(false)
                .authority("USER")
                .build();

        Board saveBoard = boardRepository.save(board);

        Optional<Board> optionalBoard = boardRepository.searchByIdAndStatus(saveBoard.getId());

        Assertions.assertNotNull(optionalBoard.get());
    }

    @Test
    @DisplayName("상위 게시판 및 하위 게시판 조회 테스트")
    void searchTreeListTest() {
        Board parent = Board.builder()
                .name("parent")
                .status(true)
                .url("/parent")
                .icon("parent")
                .sort(0)
                .display(false)
                .authority("USER")
                .build();

        Board saveParent = boardRepository.save(parent);

        Board child = Board.builder()
                .name("child")
                .parent(saveParent)
                .status(true)
                .url("/child")
                .icon("child")
                .sort(0)
                .display(false)
                .authority("USER")
                .build();

        boardRepository.save(child);

        Assertions.assertNotNull(boardRepository.searchTreeList("all"));
    }

    @Test
    @DisplayName("하위 게시판 존재여부 조회 테스트")
    void subBoardExistTest() {
        Board parent = Board.builder()
                .name("parent")
                .status(true)
                .url("/parent")
                .icon("parent")
                .sort(0)
                .display(false)
                .authority("USER")
                .build();

        Board saveParent = boardRepository.save(parent);

        Board child = Board.builder()
                .name("child")
                .parent(saveParent)
                .status(true)
                .url("/child")
                .icon("child")
                .sort(0)
                .display(false)
                .authority("USER")
                .build();

        Board saveChild = boardRepository.save(child);

        Assertions.assertTrue(boardRepository.subBoardExist(saveParent.getId()));
    }

    @Test
    @DisplayName("포스트 존재여부 조회 테스트")
    void postExistTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Board board = Board.builder()
                .name("test")
                .status(true)
                .url("/test")
                .icon("test")
                .sort(0)
                .display(false)
                .authority("USER")
                .build();

        Board saveBoard = boardRepository.save(board);

        Post post = Post.builder()
                .board(saveBoard)
                .profile(saveProfile)
                .title("test")
                .content("test")
                .status(true)
                .viewCount(0)
                .totalThumbsupVoteCount(0)
                .totalThumbsdownVoteCount(0)
                .build();

        postRepository.save(post);

        Assertions.assertTrue(boardRepository.postExist(saveBoard.getId()));
    }

    @Test
    @DisplayName("게시판 최하위단 게시판들만 조회하는 테스트")
    void searchSubListTest() {
        Board parent = Board.builder()
                .name("parent")
                .status(true)
                .url("/parent")
                .icon("parent")
                .sort(0)
                .display(false)
                .authority("USER")
                .build();

        Board saveParent = boardRepository.save(parent);

        Board child = Board.builder()
                .name("child")
                .parent(saveParent)
                .status(true)
                .url("/child")
                .icon("child")
                .sort(0)
                .display(false)
                .authority("USER")
                .build();

        Board saveChildBoard = boardRepository.save(child);

        List<Board> boardList = boardRepository.searchSubList();

        Assertions.assertEquals(boardList.get(0).getId(), saveChildBoard.getId());
    }

}
