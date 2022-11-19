package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.config.TestConfig;
import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.dto.PostDto;
import com.sideproject.codemoim.dto.PostInfoDto;
import com.sideproject.codemoim.dto.PostWithCommentDto;
import com.sideproject.codemoim.exception.PostNotFoundException;
import com.sideproject.codemoim.service.SearchService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@DataJpaTest
@Import({TestConfig.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PostRepositoryTest {

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

    @Autowired
    CommentRepository commentRepository;

    Post savePost;

    Tag saveTag;

    Profile saveProfile;

    Board saveBoard;

    Profile saveProfile2;

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
                .display(true)
                .authority("USER")
                .build();

        saveBoard = boardRepository.save(board);

        Tag tag = Tag
                .builder()
                .name("test")
                .profileFollowerCount(0)
                .postTagCount(0)
                .build();

        saveTag = tagRepository.save(tag);

        Post post = Post.builder()
                .board(saveBoard)
                .profile(saveProfile)
                .title("test")
                .content("test")
                .status(true)
                .viewCount(1)
                .totalThumbsupVoteCount(0)
                .totalThumbsdownVoteCount(0)
                .build();

        savePost = postRepository.save(post);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Set<Post> scraps2 = new LinkedHashSet<>();
        scraps2.add(savePost);

        Profile profile2 = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .scraps(scraps2)
                .build();

        saveProfile2 = profileRepository.save(profile2);

        PostTag postTag = PostTag.builder()
                .post(savePost)
                .tag(saveTag)
                .build();

        postTagRepository.save(postTag);
    }

    @Test
    @DisplayName("포스트 아이디와 상태값을 사용한 포스트 조회 테스트")
    void searchPostByIdAndStatusTest() {
        Optional<Post> optionalPost = postRepository.searchPostByIdAndStatus(savePost.getId());

        Assertions.assertNotNull(optionalPost.get());
    }

    @Test
    @DisplayName("태그 사용 포스트 존재여부 테스트")
    void relationTagExistTest() {
        boolean postExist = postRepository.relationTagExist(saveTag.getId());

        Assertions.assertTrue(postExist);
    }

    @Test
    @DisplayName("포스트 목록 조회 테스트")
    void searchPostListTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<PostDto> postDtos = postRepository.searchPostList(pageable, "normal", saveBoard.getId());

        Assertions.assertEquals(postDtos.getTotalElements(), 1);
        Assertions.assertEquals(postDtos.getContent().get(0).getId(), savePost.getId());
    }

    @Test
    @DisplayName("포스트 정보 조회 테스트")
    void searchPostInfoTest() {
        PostInfoDto postInfoDto = postRepository.searchPostInfo(savePost.getId(), saveProfile.getId());

        Assertions.assertEquals(postInfoDto.getId(), savePost.getId());
        Assertions.assertEquals(postInfoDto.getProfileId(), saveProfile.getId());
    }

    @Test
    @DisplayName("포스트 아이디, 프로필 아이디, 상태값을 사용한 포스트 조회 테스트")
    void searchPostByIdAndProfileIdAndStatusTest() {
        Post post = postRepository.searchPostByIdAndProfileIdAndStatus(savePost.getId(), saveProfile.getId()).orElseThrow(() -> {
            throw new PostNotFoundException("Post Not Found");
        });

        Assertions.assertEquals(post.getId(), savePost.getId());
        Assertions.assertEquals(post.getProfile().getId(), saveProfile.getId());
    }

    @Test
    @DisplayName("포스트 스크랩 개수 조회 테스트")
    void getScrapCountTest() {
        saveProfile.addPost(savePost);

        long scrapCount = postRepository.getScrapCount(savePost.getId());

        Assertions.assertEquals(scrapCount, 2);
    }

    @Test
    @DisplayName("포스트 존재 여부 조회 테스트")
    void postExistTest() {
        Assertions.assertEquals(postRepository.postExist(), true);
    }

    @Test
    @DisplayName("특정 태그가 포함된 포스트 리스트 조회 테스트")
    void searchTagPostTest() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<PostDto> postDtoPage = postRepository.searchTagPost(pageable, "latest", saveTag.getId());
        String tagName = postDtoPage.getContent().get(0).getTagNames().get(0);

        Assertions.assertEquals(tagName, "test");
    }

    @Test
    @DisplayName("대시보드 고정 포스트 리스트 조회 테스트")
    void searchDashboardFixedPostListTest() {
        List<PostDto> postDtoList = postRepository.searchDashboardFixedPostList("popular");

        Assertions.assertEquals(savePost.getId(), postDtoList.get(0).getId());
    }

    @Test
    @DisplayName("대시보드 비고정 포스트 리스트 조회 테스트")
    void searchDashboardNonFixedPostListTest() {
        List<Map<String, Object>> boardInfo = postRepository.searchDashboardNonFixedPostList();

        List<PostDto> postDtoList = (List<PostDto>) boardInfo.get(0).get("postList");

        Assertions.assertEquals(savePost.getId(), postDtoList.get(0).getId());
    }

    @Test
    @DisplayName("포스트 스크랩 유저 목록 조회 테스트")
    void searchScrapUserByPostIdTest() {
        List<Profile> profileList = postRepository.searchScrapUserByPostId(savePost.getId());

        Assertions.assertEquals(saveProfile2.getId(), profileList.get(0).getId());
    }

    @Test
    @DisplayName("특정 사용자가 작성한 포스트 리스트 조회 성공 테스트")
    void searchPostListByProfileIdTest() {
        Pageable pageable = PageRequest.of(0, 5);

        Page<PostDto> postDtoPage = postRepository.searchPostListByProfileId(pageable, saveProfile.getId());

        Assertions.assertEquals(postDtoPage.getContent().get(0).getId(), savePost.getId());
    }

    @Test
    @DisplayName("특정 사용자가 작성한 코멘트의 포스트 리스트 조회 성공 테스트")
    void searchCommentPostListByProfileId_success() {
        Comment comment = Comment.builder()
                .profile(saveProfile2)
                .post(savePost)
                .content("test")
                .selectedComment(false)
                .totalThumbsupVoteCount(0)
                .totalThumbsdownVoteCount(0)
                .build();

        Comment saveComment = commentRepository.save(comment);

        Pageable pageable = PageRequest.of(0, 5);

        Page<PostWithCommentDto> postWithCommentDtos = postRepository.searchCommentPostListByProfileId(pageable, saveProfile2.getId());

        Assertions.assertEquals(postWithCommentDtos.getContent().get(0).getId(), savePost.getId());
    }

    @Test
    @DisplayName("특정 사용자가 스크랩한 포스트 리스트 조회 성공 테스트")
    void searchScrapListByProfileId_success() {
        Pageable pageable = PageRequest.of(0, 5);

        Page<PostDto> postDtoPage = postRepository.searchScrapListByProfileId(pageable, saveProfile2.getId());

        Assertions.assertEquals(postDtoPage.getContent().get(0).getId(), savePost.getId());
    }

}