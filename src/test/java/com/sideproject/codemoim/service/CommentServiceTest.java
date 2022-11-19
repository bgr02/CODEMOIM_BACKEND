package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.exception.CommentNotFoundException;
import com.sideproject.codemoim.exception.PostNotFoundException;
import com.sideproject.codemoim.exception.ProfileNotFoundException;
import com.sideproject.codemoim.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {

    @InjectMocks
    CommentService commentService;

    @Mock
    CommentRepository commentRepository;

    @Mock
    ProfileRepository profileRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    CommentVoteRepository commentVoteRepository;

    @Mock
    NotificationRepository notificationRepository;

    @Test
    @DisplayName("댓글 생성 성공 테스트")
    void createCommentTest_success() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("profileId", 1);
        commentInfo.put("postId", 1);
        commentInfo.put("content", "test");

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .contributionPoint(0)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        Post post = Post.builder().build();

        given(postRepository.searchPostByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(post));

        Comment saveComment = mock(Comment.class);

        given(commentRepository.save(any())).willReturn(saveComment);
        //when(saveComment.getId()).thenReturn(anyLong());

        Assertions.assertDoesNotThrow(() -> {
            commentService.createComment(commentInfo, anyLong());
        });
    }

    @Test
    @DisplayName("댓글 생성 실패 테스트")
    void createCommentTest_fail() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("profileId", 1);
        commentInfo.put("postId", 1);
        commentInfo.put("content", "test");

        given(postRepository.searchPostByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(null));

        Comment comment = Comment.builder().build();

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            commentService.createComment(commentInfo, anyLong());
        });

        Assertions.assertThrows(PostNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .contributionPoint(0)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            commentService.createComment(commentInfo, anyLong());
        });
    }
    
    @Test
    @DisplayName("댓글 수정 성공 테스트")
    void modifyCommentTest_success() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("profileId", 1);
        commentInfo.put("commentId", 1);
        commentInfo.put("content", "test");

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .contributionPoint(0)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        Comment comment = Comment.builder().build();

        given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(comment));

        Assertions.assertDoesNotThrow(() -> {
            commentService.modifyComment(commentInfo, anyLong());
        });
    }

    @Test
    @DisplayName("댓글 수정 실패 테스트")
    void modifyCommentTest_fail() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("profileId", 1);
        commentInfo.put("commentId", 1);
        commentInfo.put("content", "test");

        given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            commentService.modifyComment(commentInfo, anyLong());
        });

        Assertions.assertThrows(CommentNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .contributionPoint(0)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            commentService.modifyComment(commentInfo, anyLong());
        });
    }

    @Test
    @DisplayName("댓글 삭제 성공 테스트")
    void deleteCommentTest_success() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("id", 1);
        commentInfo.put("profileId", 1);

        Profile postProfile = mock(Profile.class);

        Post post = Post.builder()
                .profile(postProfile)
                .build();

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .contributionPoint(0)
                .build();

        Profile commentProfile = mock(Profile.class);

        Comment comment = mock(Comment.class);

        when(comment.getProfile()).thenReturn(commentProfile);
        when(comment.getTotalThumbsupVoteCount()).thenReturn(0);
        when(comment.getTotalThumbsdownVoteCount()).thenReturn(0);
        when(comment.getPost()).thenReturn(post);

        Notification notification = mock(Notification.class);

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);
        given(commentRepository.searchCommentByCommentId(anyLong())).willReturn(comment);
        given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(commentProfile));
        given(notificationRepository.searchNotificationByProfileIdAndCommentIdAndType(anyLong(), anyLong(), anyString())).willReturn(notification);

        Assertions.assertDoesNotThrow(() -> {
            commentService.deleteComment(commentInfo, anyLong());
        });
    }

    @Test
    @DisplayName("댓글 삭제 실패 테스트")
    void deleteCommentTest_fail() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("id", 1);
        commentInfo.put("profileId", 1);

        //Profile postProfile = mock(Profile.class);

        //Post post = Post.builder()
        //        .profile(postProfile)
        //        .build();

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .contributionPoint(0)
                .build();

        Profile commentProfile = mock(Profile.class);

        Comment comment = mock(Comment.class);

        when(comment.getProfile()).thenReturn(commentProfile);
        when(comment.getTotalThumbsupVoteCount()).thenReturn(0);
        when(comment.getTotalThumbsdownVoteCount()).thenReturn(0);
        //when(comment.getPost()).thenReturn(post);

        Notification notification = mock(Notification.class);

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);
        given(commentRepository.searchCommentByCommentId(anyLong())).willReturn(comment);
        //given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(profile));
        //given(notificationRepository.searchNotificationByProfileIdAndCommentIdAndType(anyLong(), anyLong(), anyString())).willReturn(notification);

        Assertions.assertThrows(NullPointerException.class, () -> {
            commentService.deleteComment(commentInfo, anyLong());
        });
    }

    @Test
    @DisplayName("댓글 채택 성공 테스트")
    void selectCommentTest_success() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("id", 1);
        commentInfo.put("profileId", 1);

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .contributionPoint(0)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        Comment comment = Comment.builder()
                .selectedComment(false)
                .build();

        given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(comment));

        Assertions.assertDoesNotThrow(() -> {
            commentService.selectComment(commentInfo, anyLong());
        });
    }

    @Test
    @DisplayName("댓글 채택 실패 테스트")
    void selectCommentTest_fail() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("id", 1);
        commentInfo.put("profileId", 1);

        given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            commentService.selectComment(commentInfo, anyLong());
        });

        Assertions.assertThrows(CommentNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .contributionPoint(0)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            commentService.selectComment(commentInfo, anyLong());
        });
    }
    
    @Test
    @DisplayName("댓글 투표 연관 테이블 생성 성공 테스트")
    void voteCommentProcessTest_success() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("commentId", 1);
        commentInfo.put("writerId", 1);
        commentInfo.put("profileId", 2);
        commentInfo.put("voteCount", 1);

        given(commentVoteRepository.searchCommentVoteByCommentIdAndProfileId(anyLong(), anyLong())).willReturn(null);

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .contributionPoint(0)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        Profile writerProfile = mock(Profile.class);

        given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(writerProfile));

        Comment comment = Comment.builder().build();

        given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(comment));

        Assertions.assertDoesNotThrow(() -> {
            commentService.voteCommentProcess(commentInfo, anyLong());
        });
    }

    @Test
    @DisplayName("댓글 투표 연관 테이블 생성 실패 테스트")
    void voteCommentProcessTest_fail() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("commentId", 1);
        commentInfo.put("writerId", 1);
        commentInfo.put("profileId", 2);
        commentInfo.put("voteCount", 1);

        given(commentVoteRepository.searchCommentVoteByCommentIdAndProfileId(anyLong(), anyLong())).willReturn(null);
        given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            commentService.voteCommentProcess(commentInfo, anyLong());
        });

        Assertions.assertThrows(CommentNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .contributionPoint(0)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            commentService.voteCommentProcess(commentInfo, anyLong());
        });
    }

    @Test
    @DisplayName("댓글 투표 성공 테스트")
    void voteProcessTest_success() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("profileId", 1);
        commentInfo.put("commentId", 1);
        commentInfo.put("voteType", "up");
        commentInfo.put("voteCount", 1);

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .contributionPoint(0)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        Comment comment = Comment.builder()
                .totalThumbsupVoteCount(0)
                .totalThumbsdownVoteCount(0)
                .build();

        given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(comment));

        Assertions.assertDoesNotThrow(() -> {
            commentService.voteProcess(commentInfo, anyLong());
        });
    }

    @Test
    @DisplayName("댓글 투표 실패 테스트")
    void voteProcessTest_fail() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("profileId", 1);
        commentInfo.put("commentId", 1);
        commentInfo.put("voteType", "up");
        commentInfo.put("voteCount", 1);

        given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            commentService.voteProcess(commentInfo, anyLong());
        });

        Assertions.assertThrows(CommentNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .contributionPoint(0)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            commentService.voteProcess(commentInfo, anyLong());
        });
    }

}
