package com.sideproject.codemoim.Lock;

import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.repository.*;
import com.sideproject.codemoim.service.PostService;
import com.sideproject.codemoim.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LockTest {

    @Autowired
    PostService postService;

    @Autowired
    ProfileService profileService;

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

    Profile saveProfile;
    Profile saveProfile2;
    Tag saveTag;
    Post savePost;

    @BeforeEach
    void createEntity() {
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

        saveProfile = profileRepository.save(profile);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile2 = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .build();

        saveProfile2 = profileRepository.save(profile2);

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

        saveTag = tagRepository.save(tag);

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
    }

    @Test
    @DisplayName("포스트 투표 충돌 테스트")
    void postVoteTest() throws InterruptedException {
        Map<String, Object> voteInfo = new HashMap<>();
        voteInfo.put("postId", savePost.getId().intValue());
        voteInfo.put("voteType", "up");
        voteInfo.put("voteCount", 1);

        final ExecutorService service = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 20; i++) {
            service.execute(() -> {
                try {
                    postService.voteProcess(voteInfo, anyLong());
                    System.out.println("성공");
                } catch (ObjectOptimisticLockingFailureException oolfe) {
                    System.out.println("충돌감지");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
        }

        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("태그 팔로우 충돌 테스트")
    void tagFollowTest() throws InterruptedException {
        Map<String, Object> followInfo = new HashMap<>();

        followInfo.put("id",  saveProfile.getId().intValue());
        followInfo.put("tagName", saveTag.getName());

        final ExecutorService service = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 20; i++) {
            service.execute(() -> {
                try {
                    profileService.followTag(followInfo, anyLong());
                    System.out.println("성공");
                } catch (ObjectOptimisticLockingFailureException oolfe) {
                    System.out.println("충돌감지");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
        }

        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);
    }

    @Test
    @DisplayName("포스트 스크랩 충돌 테스트")
    void scrapPostTest() throws InterruptedException {
        Map<String, Object> scrapInfo = new HashMap<>();

        scrapInfo.put("postId", savePost.getId().intValue());
        scrapInfo.put("writerId", saveProfile.getId().intValue());
        scrapInfo.put("profileId", saveProfile2.getId().intValue());

        final ExecutorService service = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 20; i++) {
            service.execute(() -> {
                try {
                    postService.scrapPost(scrapInfo, anyLong());
                    System.out.println("성공");
                } catch (ObjectOptimisticLockingFailureException oolfe) {
                    System.out.println("충돌감지");
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            });
        }

        service.shutdown();
        service.awaitTermination(1, TimeUnit.MINUTES);
    }

}
