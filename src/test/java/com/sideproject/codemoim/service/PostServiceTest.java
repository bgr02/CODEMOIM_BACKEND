package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.dto.PostDto;
import com.sideproject.codemoim.exception.BoardNotFoundException;
import com.sideproject.codemoim.exception.PostNotFoundException;
import com.sideproject.codemoim.exception.ProfileNotFoundException;
import com.sideproject.codemoim.exception.TagNotFoundException;
import com.sideproject.codemoim.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    PostService postService;

    @Mock
    PostRepository postRepository;

    @Mock
    BoardRepository boardRepository;

    @Mock
    ProfileRepository profileRepository;

    @Mock
    TagRepository tagRepository;

    @Mock
    PostVoteRepository postVoteRepository;

    @Mock
    NotificationRepository notificationRepository;

    @Test
    @DisplayName("포스트 조회수 갱신 성공 테스트")
    void modifyViewCountTest_success() {
        Map<String, Object> postInfo = new HashMap<>();

        postInfo.put("id", 1);

        Post post = Post.builder()
                .viewCount(0)
                .build();

        given(postRepository.searchPostByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(post));

        Assertions.assertDoesNotThrow(() -> {
            postService.modifyViewCount(postInfo);
        });

        Assertions.assertEquals(post.getViewCount(), 1);
    }

    @Test
    @DisplayName("포스트 조회수 갱신 실패 테스트")
    void modifyViewCountTest_fail() {
        Map<String, Object> postInfo = new HashMap<>();

        postInfo.put("id", 1);

        given(postRepository.searchPostByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(null));

        Assertions.assertThrows(PostNotFoundException.class, () -> {
            postService.modifyViewCount(postInfo);
        });
    }

    @Test
    @DisplayName("포스트 작성 성공 테스트")
    void createPostTest_success() {
        Map<String, Object> postInfo = new HashMap<>();

        postInfo.put("boardId", 1);
        postInfo.put("profileId", 1);
        postInfo.put("title", "test");

        User user = mock(User.class);

        Role role = Role.builder()
                .name(RoleName.ROLE_USER)
                .build();

        List<Role> roles = new ArrayList<>();
        roles.add(role);

        when(user.getRoles()).thenReturn(roles);

        Profile profile = Profile.builder()
                .user(user)
                .contributionPoint(0)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        Map<String, List<String>> tagNames = new HashMap<>();

        List<String> existTags = new ArrayList<>();

        existTags.add("test");

        tagNames.put("existTags", existTags);

        List<String> newTags = new ArrayList<>();

        newTags.add("test");

        tagNames.put("newTags", newTags);

        List<String> sortTags = new ArrayList<>();

        sortTags.add("test");

        tagNames.put("sortTags", sortTags);

        postInfo.put("tagNames", tagNames);
        postInfo.put("content", "test");

        Board board = Board.builder()
                .authority("USER")
                .build();

        given(boardRepository.searchByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(board));

        Post post = Post.builder()
                .build();

        given(postRepository.save(any())).willReturn(post);

        Tag tag = Tag.builder()
                .postTagCount(0)
                .build();

        given(tagRepository.findByName(anyString())).willReturn(tag);

        Tag tag2 = Tag.builder().build();

        given(tagRepository.save(any())).willReturn(tag2);

        Assertions.assertDoesNotThrow(() -> {
            postService.createPost(postInfo, anyLong());
        });
    }

    @Test
    @DisplayName("포스트 작성 실패 테스트")
    void createPostTest_fail() {
        Map<String, Object> postInfo = new HashMap<>();

        postInfo.put("boardId", 1);
        postInfo.put("profileId", 1);
        postInfo.put("title", "test");

        Map<String, List<String>> tagNames = new HashMap<>();

        List<String> existTags = new ArrayList<>();

        existTags.add("test");

        tagNames.put("existTags", existTags);

        List<String> newTags = new ArrayList<>();

        newTags.add("test");

        tagNames.put("newTags", newTags);

        List<String> sortTags = new ArrayList<>();

        sortTags.add("test");

        tagNames.put("sortTags", sortTags);

        postInfo.put("tagNames", tagNames);
        postInfo.put("content", "test");

        given(boardRepository.searchByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(null));

        Tag tag = Tag.builder().build();

        //given(tagRepository.findByName(anyString())).willReturn(tag);
        //given(tagRepository.save(any())).willReturn(null);

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            postService.createPost(postInfo, anyLong());
        });

        Assertions.assertThrows(BoardNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .contributionPoint(0)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            postService.createPost(postInfo, anyLong());
        });
    }

    @Test
    @DisplayName("포스트 수정 성공 테스트")
    void modifyPostTest_success() {
        User user = mock(User.class);

        Role role = Role.builder()
                .name(RoleName.ROLE_USER)
                .build();

        List<Role> roles = new ArrayList<>();
        roles.add(role);

        when(user.getRoles()).thenReturn(roles);

        Profile profile = Profile.builder()
                .user(user)
                .contributionPoint(0)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        Map<String, Object> postInfo = new HashMap<>();

        postInfo.put("boardId", 1);

        Map<String, List<String>> tagNames = new HashMap<>();

        List<String> existTags = new ArrayList<>();

        existTags.add("test");

        tagNames.put("existTags", existTags);

        List<String> newTags = new ArrayList<>();

        newTags.add("test");

        tagNames.put("newTags", newTags);

        List<String> sortTags = new ArrayList<>();

        sortTags.add("test");

        tagNames.put("sortTags", sortTags);

        postInfo.put("tagNames", tagNames);
        postInfo.put("title", "test");

        List<String> deleteImgUrls = new ArrayList<>();

        postInfo.put("deleteImgUrls", deleteImgUrls);
        postInfo.put("postId", 1);
        postInfo.put("profileId", 1);
        postInfo.put("content", "test");

        Post post = Post.builder().build();

        given(postRepository.searchPostByIdAndProfileIdAndStatus(anyLong(), anyLong())).willReturn(Optional.ofNullable(post));

        Board board = Board.builder()
                .authority("USER")
                .build();

        given(boardRepository.searchByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(board));

        Tag tag = Tag.builder()
                .postTagCount(0)
                .build();

        given(tagRepository.findByName(anyString())).willReturn(tag);

        Tag tag2 = Tag.builder().build();

        given(tagRepository.save(any())).willReturn(tag2);

        Assertions.assertDoesNotThrow(() -> {
            postService.modifyPost(postInfo, anyLong());
        });
    }

    @Test
    @DisplayName("포스트 수정 실패 테스트")
    void modifyPostTest_fail() {
        Map<String, Object> postInfo = new HashMap<>();

        postInfo.put("boardId", 1);

        Map<String, List<String>> tagNames = new HashMap<>();

        List<String> existTags = new ArrayList<>();

        existTags.add("test");

        tagNames.put("existTags", existTags);

        List<String> newTags = new ArrayList<>();

        newTags.add("test");

        tagNames.put("newTags", newTags);

        List<String> sortTags = new ArrayList<>();

        sortTags.add("test");

        tagNames.put("sortTags", sortTags);

        postInfo.put("tagNames", tagNames);
        postInfo.put("title", "test");

        List<String> deleteImgUrls = new ArrayList<>();

        postInfo.put("deleteImgUrls", deleteImgUrls);
        postInfo.put("postId", 1);
        postInfo.put("profileId", 1);
        postInfo.put("content", "test");

        given(postRepository.searchPostByIdAndProfileIdAndStatus(anyLong(), anyLong())).willReturn(Optional.ofNullable(null));
        given(boardRepository.searchByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(null));
        //given(tagRepository.findByName(anyString())).willReturn(null);
        //given(tagRepository.save(any())).willReturn(null);

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            postService.modifyPost(postInfo, anyLong());
        });

        Assertions.assertThrows(PostNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .contributionPoint(0)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            postService.modifyPost(postInfo, anyLong());
        });

        Assertions.assertThrows(BoardNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .contributionPoint(0)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            Post post = Post.builder().build();

            given(postRepository.searchPostByIdAndProfileIdAndStatus(anyLong(), anyLong())).willReturn(Optional.ofNullable(post));

            postService.modifyPost(postInfo, anyLong());
        });
    }

    @Test
    @DisplayName("포스트 삭제 성공 테스트")
    void deletePostTest_success() {
        Map<String, Object> deleteInfo = new HashMap<>();

        deleteInfo.put("postId", 1);
        deleteInfo.put("profileId", 1);

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .contributionPoint(0)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        Post post = mock(Post.class);

        given(postRepository.searchPostByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(post));

        List<Profile> profileList = new ArrayList<>();

        Set<Post> scraps = new LinkedHashSet<>();

        scraps.add(post);

        Profile scrapProfile = Profile.builder()
                .scraps(scraps)
                .build();

        profileList.add(scrapProfile);

        given(postRepository.searchScrapUserByPostId(anyLong())).willReturn(profileList);

        Tag tag = Tag.builder()
                .build();

        Assertions.assertDoesNotThrow(() -> {
            postService.deletePost(deleteInfo, anyLong());
        });
    }

    @Test
    @DisplayName("포스트 삭제 실패 테스트")
    void deletePostTest_fail() {
        Map<String, Object> deleteInfo = new HashMap<>();

        deleteInfo.put("postId", 1);
        deleteInfo.put("profileId", 1);

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            postService.deletePost(deleteInfo, anyLong());
        });

        Assertions.assertThrows(PostNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .contributionPoint(0)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            postService.deletePost(deleteInfo, anyLong());
        });
    }

    @Test
    @DisplayName("포스트 목록 조회 성공 테스트")
    void searchPostListTest_success() {
        Pageable pageable = PageRequest.of(0, 10);

        Assertions.assertDoesNotThrow(() -> {
            postService.searchPostList(pageable, "normal", 1L);
        });
    }

    @Test
    @DisplayName("포스트 정보 조회 성공 테스트")
    void searchInfoPost_success() {
        Assertions.assertDoesNotThrow(() -> {
            postService.searchInfoPost(1L, 1L);
        });
    }

    @Test
    @DisplayName("포스트 투표 연관 테이블 생성 성공 테스트")
    void votePostProcessTest_success() {
        Map<String, Object> voteInfo = new HashMap<>();

        voteInfo.put("postId", 1);
        voteInfo.put("writerId", 1);
        voteInfo.put("profileId", 1);
        voteInfo.put("voteCount", 1);

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .contributionPoint(0)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        given(postVoteRepository.searchPostVoteByPostIdAndProfileId(anyLong(), anyLong())).willReturn(null);

        Post post = Post.builder().build();

        given(postRepository.searchPostByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(post));

        Profile writer = Profile.builder()
                .contributionPoint(0)
                .build();

        //given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(writer));

        Assertions.assertDoesNotThrow(() -> {
            postService.votePostProcess(voteInfo, anyLong());
        });
    }

    @Test
    @DisplayName("포스트 투표 연관 테이블 생성 실패 테스트")
    void votePostProcessTest_fail() {
        Map<String, Object> voteInfo = new HashMap<>();

        voteInfo.put("postId", 1);
        voteInfo.put("writerId", 1);
        voteInfo.put("profileId", 1);
        voteInfo.put("voteCount", 1);

        given(postVoteRepository.searchPostVoteByPostIdAndProfileId(anyLong(), anyLong())).willReturn(null);
        given(postRepository.searchPostByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(null));
        //given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            postService.votePostProcess(voteInfo, anyLong());
        });

        Assertions.assertThrows(PostNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .contributionPoint(0)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            postService.votePostProcess(voteInfo, anyLong());
        });
    }

    @Test
    @DisplayName("포스트 투표 성공 테스트")
    void voteProcessTest_success() {
        Map<String, Object> voteInfo = new HashMap<>();

        voteInfo.put("postId", 1);
        voteInfo.put("profileId", 1);
        voteInfo.put("voteType", "up");
        voteInfo.put("voteCount", 0);

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .contributionPoint(0)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        Post post = Post.builder()
                .totalThumbsupVoteCount(0)
                .totalThumbsdownVoteCount(0)
                .build();

        given(postRepository.searchPostByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(post));

        Assertions.assertDoesNotThrow(() -> {
            postService.voteProcess(voteInfo, anyLong());
        });
    }

    @Test
    @DisplayName("포스트 투표 실패 테스트")
    void voteProcessTest_fail() {
        Map<String, Object> voteInfo = new HashMap<>();

        voteInfo.put("postId", 1);
        voteInfo.put("profileId", 1);
        voteInfo.put("voteType", "up");
        voteInfo.put("voteCount", 0);

        given(postRepository.searchPostByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(null));

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            postService.voteProcess(voteInfo, anyLong());
        });

        Assertions.assertThrows(PostNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .contributionPoint(0)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            postService.voteProcess(voteInfo, anyLong());
        });
    }

    @Test
    @DisplayName("포스트 스크랩 성공 테스트")
    void scrapPostTest_success() {
        Map<String, Object> scrapInfo = new HashMap<>();

        scrapInfo.put("postId", 1);
        scrapInfo.put("writerId", 1);
        scrapInfo.put("profileId", 1);
        scrapInfo.put("type", "scrap");

        Set<Post> scraps = new LinkedHashSet<>();

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .scraps(scraps)
                .contributionPoint(0)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        given(profileRepository.searchScrapByProfileAndPost(anyLong(), anyLong())).willReturn(null);

        Post post = Post.builder().build();

        given(postRepository.searchPostByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(post));

        Profile writer = Profile.builder()
                .contributionPoint(0)
                .scraps(scraps)
                .build();

        //given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(writer));

        Assertions.assertDoesNotThrow(() -> {
            postService.scrapPost(scrapInfo, anyLong());
        });
    }

    @Test
    @DisplayName("포스트 스크랩 실패 테스트")
    void scrapPostTest_fail() {
        Map<String, Object> scrapInfo = new HashMap<>();

        scrapInfo.put("postId", 1);
        scrapInfo.put("writerId", 1);
        scrapInfo.put("profileId", 1);

        given(profileRepository.searchScrapByProfileAndPost(anyLong(), anyLong())).willReturn(null);
        given(postRepository.searchPostByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(null));
        //given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            postService.scrapPost(scrapInfo, anyLong());
        });

        Assertions.assertThrows(PostNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .contributionPoint(0)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            postService.scrapPost(scrapInfo, anyLong());
        });
    }

    @Test
    @DisplayName("태그가 포함된 포스트 리스트 조회 성공 테스트")
    void searchTagPostTest_success() {
        Tag tag = Tag.builder()
                .name("test")
                .build();

        given(tagRepository.findByName(anyString())).willReturn(tag);

        Pageable pageable = PageRequest.of(0, 10);

        Assertions.assertDoesNotThrow(() -> {
            postService.searchTagPost(pageable, "normal", "test");
        });
    }

    @Test
    @DisplayName("태그가 포함된 포스트 리스트 조회 실패 테스트")
    void searchTagPostTest_fail() {
        Pageable pageable = PageRequest.of(0, 10);

        Assertions.assertThrows(TagNotFoundException.class, () -> {
            postService.searchTagPost(pageable, "normal", "test");
        });
    }

    @Test
    @DisplayName("대시보드 고정 포스트 조회 성공 테스트")
    void searchDashboardFixedPostListTest_success() {
        List<PostDto> postDtoList = new ArrayList<>();

        given(postRepository.searchDashboardFixedPostList(anyString())).willReturn(postDtoList);

        Assertions.assertDoesNotThrow(() -> {
            postService.searchDashboardFixedPostList("test");
        });
    }

    @Test
    @DisplayName("대시보드 비고정 포스트 조회 성공 테스트")
    void searchDashboardNonFixedPostListTest_success() {
        List<Map<String, Object>> postDtoList = new ArrayList<>();

        given(postRepository.searchDashboardNonFixedPostList()).willReturn(postDtoList);

        Assertions.assertDoesNotThrow(() -> {
            postService.searchDashboardNonFixedPostList();
        });
    }

    @Test
    @DisplayName("특정 사용자가 작성한 포스트 리스트 조회 성공 테스트")
    void searchPostListByProfileId_success() {
        Pageable pageable = PageRequest.of(0, 5);

        given(postRepository.searchPostListByProfileId(any(), anyLong())).willReturn(new PageImpl<>(new ArrayList<>(), pageable, 0));

        Assertions.assertDoesNotThrow(() -> {
            postService.searchPostListByProfileId(pageable, 1L);
        });
    }

    @Test
    @DisplayName("특정 사용자가 작성한 코멘트의 포스트 리스트 조회 성공 테스트")
    void searchCommentPostListByProfileId_success() {
        Pageable pageable = PageRequest.of(0, 5);

        given(postRepository.searchCommentPostListByProfileId(any(), anyLong())).willReturn(new PageImpl<>(new ArrayList<>(), pageable, 0));

        Assertions.assertDoesNotThrow(() -> {
            postService.searchCommentPostListByProfileId(pageable, 1L);
        });
    }

    @Test
    @DisplayName("특정 사용자가 스크랩한 포스트 리스트 조회 성공 테스트")
    void searchScrapListByProfileId_success() {
        Pageable pageable = PageRequest.of(0, 5);

        given(postRepository.searchScrapListByProfileId(any(), anyLong())).willReturn(new PageImpl<>(new ArrayList<>(), pageable, 0));

        Assertions.assertDoesNotThrow(() -> {
            postService.searchScrapListByProfileId(pageable, 1L);
        });
    }

}
