package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.config.TestConfig;
import com.sideproject.codemoim.domain.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({TestConfig.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostVoteRepositoryTest {

    @Autowired
    PostVoteRepository postVoteRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostTagRepository postTagRepository;

    Post savePost;

    Profile saveProfile;

    PostVote savePostVote;

    @BeforeEach
    void createPost() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Set<Post> scraps = new LinkedHashSet<>();

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .scraps(scraps)
                .build();

        saveProfile = profileRepository.save(profile);

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

        Tag tag = Tag
                .builder()
                .name("test")
                .profileFollowerCount(0)
                .postTagCount(0)
                .build();

        Tag saveTag = tagRepository.save(tag);

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

        savePost = postRepository.save(post);

        PostTag postTag = PostTag.builder()
                .post(savePost)
                .tag(saveTag)
                .build();

        postTagRepository.save(postTag);

        PostVote postVote= PostVote.builder()
                .profile(saveProfile)
                .post(savePost)
                .voteCount(0)
                .build();

        savePostVote = postVoteRepository.save(postVote);
    }

    @Test
    @DisplayName("포스트 아이디와 프로필 아이디를 사용한 포스트-투표 엔티티 검색 테스트")
    void searchPostVoteByPostIdAndProfileIdTest() {
        PostVote findPostVote = postVoteRepository.searchPostVoteByPostIdAndProfileId(savePost.getId(), saveProfile.getId());

        Assertions.assertEquals(savePostVote.getId(), findPostVote.getId());
    }

    @Test
    @DisplayName("포스트 추천 이력 일괄 삭제")
    void deletePostVoteByPostTest() {
        List<PostVote> postVoteList = postVoteRepository.findAll();

        int count = postVoteRepository.deletePostVoteByPost(savePost);

        Assertions.assertEquals(postVoteList.size(), count);
    }

    @Test
    @DisplayName("프로필 아이디를 사용한 포스트 추천 조회 테스트")
    void searchPostVoteByProfileIdTest() {
        List<PostVote> postVoteList = postVoteRepository.searchPostVoteByProfileId(saveProfile.getId());

        PostVote postVote = postVoteList.get(0);

        Assertions.assertEquals(savePostVote.getId(), postVote.getId());
    }

}