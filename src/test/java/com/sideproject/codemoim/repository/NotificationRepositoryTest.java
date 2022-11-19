package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.config.TestConfig;
import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.dto.NotificationDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.List;

@DataJpaTest
@Import({TestConfig.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class NotificationRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("읽지 않은 알림 개수 조회 테스트")
    void searchNonReadNotificationCountByUserIdTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Profile provider = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .build();

        Profile saveProvider = profileRepository.save(provider);

        Notification notification = Notification.builder()
                .profile(saveProfile)
                .provider(saveProvider)
                .content("Test Content")
                .read(false)
                .type("test")
                .build();

        Notification notification2 = Notification.builder()
                .profile(saveProfile)
                .provider(saveProvider)
                .content("Test Content")
                .read(false)
                .type("test")
                .build();

        notificationRepository.save(notification);
        notificationRepository.save(notification2);

        Long count = notificationRepository.searchNonReadNotificationCountByUserId(saveUser.getId());

        Assertions.assertEquals(count, 2);
    }

    @Test
    @DisplayName("알림 정보 조회 테스트")
    void searchNotificationByUserIdTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Profile provider = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .build();

        Profile saveProvider = profileRepository.save(provider);

        Notification notification = Notification.builder()
                .profile(saveProfile)
                .provider(saveProvider)
                .content("Test Content")
                .read(false)
                .type("test")
                .build();

        Notification notification2 = Notification.builder()
                .profile(saveProfile)
                .provider(saveProvider)
                .content("Test Content")
                .read(false)
                .type("test")
                .build();

        notificationRepository.save(notification);
        notificationRepository.save(notification2);

        List<NotificationDto> notificationDtos = notificationRepository.searchNotificationByUserId(saveUser.getId());

        Assertions.assertEquals(notificationDtos.size(), 2);
    }

//    @Test
//    void updateNotificationTest() {
//        Long notificationId = 1L;
//
//        Optional<Notification> optionalNotificationBefore = notificationRepository.findById(notificationId);
//
//        if(optionalNotificationBefore.isPresent()) {
//            Notification notificationBefore = optionalNotificationBefore.get();
//            Boolean readBefore = notificationBefore.getRead();
//
//            notificationRepository.updateNotification(notificationId);
//
//            entityManager.clear();
//
//            Optional<Notification> optionalNotificationAfter = notificationRepository.findById(notificationId);
//
//            if(optionalNotificationAfter.isPresent()) {
//                Notification notificationAfrer = optionalNotificationAfter.get();
//                Boolean readAfter = notificationAfrer.getRead();
//
//                Assertions.assertNotEquals(readBefore, readAfter);
//            }
//        }
//    }

    @Test
    @DisplayName("프로필 아이디와 댓글 아이디, 타입을 사용한 알림 조회 테스트")
    void searchNotificationByProfileIdAndCommentIdAndTypeTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Profile provider = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .build();

        Profile saveProvider = profileRepository.save(provider);

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

        Post savePost = postRepository.save(post);

        Comment comment = Comment.builder()
                .profile(saveProvider)
                .post(savePost)
                .content("test")
                .selectedComment(false)
                .totalThumbsupVoteCount(0)
                .totalThumbsdownVoteCount(0)
                .build();

        Comment saveComment = commentRepository.save(comment);

        Notification notification = Notification.builder()
                .profile(saveProfile)
                .provider(saveProvider)
                .comment(saveComment)
                .content("Test Content")
                .read(false)
                .type("test")
                .build();

        Notification saveNotification = notificationRepository.save(notification);

        Notification findNotification = notificationRepository.searchNotificationByProfileIdAndCommentIdAndType(saveProfile.getId(), saveComment.getId(), "test");

        Assertions.assertEquals(saveNotification.getId(), findNotification.getId());
    }

    @Test
    @DisplayName("프로필 아이디와 프로바이더 아이디, 댓글 아이디, 타입을 사용한 알림 조회 테스트")
    void searchNotificationByProfileIdAndProviderIdAndCommentIdAndTypeTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Profile provider = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .build();

        Profile saveProvider = profileRepository.save(provider);

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

        Post savePost = postRepository.save(post);

        Comment comment = Comment.builder()
                .profile(saveProvider)
                .post(savePost)
                .content("test")
                .selectedComment(false)
                .totalThumbsupVoteCount(0)
                .totalThumbsdownVoteCount(0)
                .build();

        Comment saveComment = commentRepository.save(comment);

        Notification notification = Notification.builder()
                .profile(saveProfile)
                .provider(saveProvider)
                .comment(saveComment)
                .content("Test Content")
                .read(false)
                .type("test")
                .build();

        Notification saveNotification = notificationRepository.save(notification);

        Notification findNotification = notificationRepository.searchNotificationByProfileIdAndProviderIdAndCommentIdAndType(saveProfile.getId(), saveProvider.getId(), saveComment.getId(), "test");

        Assertions.assertEquals(saveNotification.getId(), findNotification.getId());
    }
    
    @Test
    @DisplayName("프로필 아이디와 포스트 아이디, 타입을 사용한 알림 조회 테스트")
    void searchNotificationListByProfileIdAndPostIdAndTypeTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Profile provider = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .build();

        Profile saveProvider = profileRepository.save(provider);

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

        Post savePost = postRepository.save(post);

        Notification notification = Notification.builder()
                .profile(saveProfile)
                .provider(saveProvider)
                .post(savePost)
                .content("Test Content")
                .read(false)
                .type("test")
                .build();

        Notification saveNotification = notificationRepository.save(notification);

        List<Notification> notificationList = notificationRepository.searchNotificationListByProviderIdAndPostIdAndType(saveProvider.getId(), savePost.getId(), "test");

        Assertions.assertEquals(saveNotification.getId(), notificationList.get(0).getId());
    }

    @Test
    @DisplayName("프로필 아이디와 프로바이더 아이디, 포스트 아이디 타입을 사용한 알림 테스트")
    void searchNotificationByProfileIdAndProviderIdAndPostIdAndTypeTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Profile provider = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .build();

        Profile saveProvider = profileRepository.save(provider);

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

        Post savePost = postRepository.save(post);

        Notification notification = Notification.builder()
                .profile(saveProfile)
                .provider(saveProvider)
                .post(savePost)
                .content("Test Content")
                .read(false)
                .type("test")
                .build();

        Notification saveNotification = notificationRepository.save(notification);

        Notification findNotification = notificationRepository.searchNotificationByProfileIdAndProviderIdAndPostIdAndType(saveProfile.getId(), saveProvider.getId(), savePost.getId(), "test");

        Assertions.assertEquals(saveNotification.getId(), findNotification.getId());
    }

    @Test
    @DisplayName("포스트와 타입을 사용한 알림 삭제 테스트")
    void deleteNotificationByPostAndTypeTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Profile provider = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .build();

        Profile saveProvider = profileRepository.save(provider);

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

        Post savePost = postRepository.save(post);

        Notification notification = Notification.builder()
                .profile(saveProfile)
                .provider(saveProvider)
                .post(savePost)
                .content("Test Content")
                .read(false)
                .type("test")
                .build();

        Notification saveNotification = notificationRepository.save(notification);

        List<Notification> notificationList = notificationRepository.findAll();

        int count = notificationRepository.deleteNotificationByPostAndType(savePost, "test");

        Assertions.assertEquals(notificationList.size(), count);
    }

    @Test
    @DisplayName("코멘트를 사용한 알림 삭제 테스트")
    void deleteNotificationByCommentTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Profile provider = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .build();

        Profile saveProvider = profileRepository.save(provider);

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

        Post savePost = postRepository.save(post);

        Comment comment = Comment.builder()
                .post(savePost)
                .profile(saveProvider)
                .content("test")
                .selectedComment(false)
                .totalThumbsupVoteCount(0)
                .totalThumbsdownVoteCount(0)
                .build();

        Comment saveComment = commentRepository.save(comment);

        Notification notification = Notification.builder()
                .profile(saveProfile)
                .provider(saveProvider)
                .comment(saveComment)
                .content("Test Content")
                .read(false)
                .type("test")
                .build();

        notificationRepository.save(notification);

        int deleteNotificationCount = notificationRepository.deleteNotificationByCommentAndType(saveComment, "test");

        Assertions.assertEquals(deleteNotificationCount, 1);
    }

    @Test
    @DisplayName("프로필과 타입을 사용한 알림 삭제 테스트")
    void deleteNotificationByProfileAndTypeTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Profile provider = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .build();

        Profile saveProvider = profileRepository.save(provider);

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

        Post savePost = postRepository.save(post);

        Comment comment = Comment.builder()
                .post(savePost)
                .profile(saveProvider)
                .content("test")
                .selectedComment(false)
                .totalThumbsupVoteCount(0)
                .totalThumbsdownVoteCount(0)
                .build();

        Comment saveComment = commentRepository.save(comment);

        Notification notification = Notification.builder()
                .profile(saveProfile)
                .provider(saveProvider)
                .comment(saveComment)
                .content("Test Content")
                .read(false)
                .type("test")
                .build();

        notificationRepository.save(notification);

        int deleteNotificationCount = notificationRepository.deleteNotificationByProfileAndType(saveProfile, "test");

        Assertions.assertEquals(deleteNotificationCount, 1);
    }

}