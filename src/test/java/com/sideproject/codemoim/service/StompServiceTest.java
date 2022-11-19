package com.sideproject.codemoim.service;

import antlr.build.ANTLR;
import com.sideproject.codemoim.config.StompEventListener;
import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.dto.NotificationDto;
import com.sideproject.codemoim.exception.CommentNotFoundException;
import com.sideproject.codemoim.exception.NotificationNotFoundException;
import com.sideproject.codemoim.exception.PostNotFoundException;
import com.sideproject.codemoim.exception.ProfileNotFoundException;
import com.sideproject.codemoim.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StompServiceTest {

    @InjectMocks
    StompService stompService;

    @Mock
    NotificationRepository notificationRepository;

    @Mock
    ProfileRepository profileRepository;

    @Mock
    CommentRepository commentRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    ProfileFollowRepository profileFollowRepository;

    @Mock
    RabbitTemplate rabbitTemplate;

    //@Mock
    //StompEventListener stompEventListener;

    @Test
    @DisplayName("댓글 알림 성공 테스트")
    void commentAlarmTest_success() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("profileId", 1);
        commentInfo.put("providerId", 1);
        commentInfo.put("postId", 1);
        commentInfo.put("commentId", 1);

        Profile profile = Profile.builder().build();

        given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(profile));

        Profile provider = Profile.builder().build();

        given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(provider));

        Comment comment = Comment.builder().build();

        given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(comment));

        Notification saveNotification = mock(Notification.class);

        given(notificationRepository.save(any())).willReturn(saveNotification);

        //stompEventListener.registerBrowserSession("userId", 1L);

        Assertions.assertDoesNotThrow(() -> {
            stompService.commentAlarm(commentInfo);
        });
    }

    @Test
    @DisplayName("댓글 알림 실패 테스트")
    void commentAlarmTest_fail() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("profileId", 1);
        commentInfo.put("providerId", 1);
        commentInfo.put("postId", 1);
        commentInfo.put("commentId", 1);

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            stompService.commentAlarm(commentInfo);
        });

        Assertions.assertThrows(CommentNotFoundException.class, () -> {
            Profile profile = Profile.builder().build();

            given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(profile));

            Profile provider = Profile.builder().build();

            given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(provider));

            stompService.commentAlarm(commentInfo);
        });
    }

    @Test
    @DisplayName("댓글 알림 취소 성공 테스트")
    void commentAlarmCancelTest_success() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("profileId", 1);
        commentInfo.put("read", false);
        commentInfo.put("notificationId", 1);

        Assertions.assertDoesNotThrow(() -> {
            stompService.commentAlarmCancel(commentInfo);
        });
    }

    @Test
    @DisplayName("댓글 알림 취소 실패 테스트")
    void commentAlarmCancelTest_fail() {
        Map<String, Object> commentInfo = new HashMap<>();

        Assertions.assertThrows(NullPointerException.class, () -> {
            stompService.commentAlarmCancel(commentInfo);
        });
    }

    @Test
    @DisplayName("댓글 추천 알림 성공 테스트")
    void commentRecommendTest_success() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("profileId", 1);
        commentInfo.put("providerId", 1);
        commentInfo.put("postId", 1);
        commentInfo.put("commentId", 1);

        Profile profile = Profile.builder().build();

        given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(profile));

        Profile provider = Profile.builder().build();

        given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(provider));

        Comment comment = Comment.builder().build();

        given(commentRepository.findById(anyLong())).willReturn(Optional.ofNullable(comment));

        Notification saveNotification = mock(Notification.class);

        given(notificationRepository.save(any())).willReturn(saveNotification);

        //stompEventListener.registerBrowserSession("userId", 1L);

        Assertions.assertDoesNotThrow(() -> {
            stompService.commentRecommend(commentInfo);
        });
    }

    @Test
    @DisplayName("댓글 추천 알림 실패 테스트")
    void commentRecommendTest_fail() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("profileId", 1);
        commentInfo.put("providerId", 1);
        commentInfo.put("postId", 1);
        commentInfo.put("commentId", 1);

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            stompService.commentAlarm(commentInfo);
        });

        Assertions.assertThrows(CommentNotFoundException.class, () -> {
            Profile profile = Profile.builder().build();

            given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(profile));

            Profile provider = Profile.builder().build();

            given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(provider));

            stompService.commentAlarm(commentInfo);
        });
    }

    @Test
    @DisplayName("댓글 알림 취소 성공 테스트")
    void commentRecommendCancelTest_success() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("profileId", 1);
        commentInfo.put("providerId", 1);
        commentInfo.put("commentId", 1);

        String type = "comment_recommend";

        Notification notification = mock(Notification.class);

        given(notificationRepository.searchNotificationByProfileIdAndProviderIdAndCommentIdAndType(anyLong(), anyLong(), anyLong(), anyString())).willReturn(notification);

        Assertions.assertDoesNotThrow(() -> {
            stompService.commentRecommendCancel(commentInfo);
        });
    }

    @Test
    @DisplayName("댓글 알림 취소 실패 테스트")
    void commentRecommendCancelTest_fail() {
        Map<String, Object> commentInfo = new HashMap<>();

        commentInfo.put("profileId", 1);
        commentInfo.put("providerId", 1);
        commentInfo.put("commentId", 1);

        String type = "comment_recommend";

        Assertions.assertThrows(NotificationNotFoundException.class, () -> {
            stompService.commentRecommendCancel(commentInfo);
        });
    }

    @Test
    @DisplayName("포스트 알림 성공 테스트")
    void postAlarmTest_success() {
        Map<String, Object> postInfo = new HashMap<>();

        postInfo.put("providerId", 1);
        postInfo.put("postId", 1);

        Profile profile = Profile.builder().build();

        given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(profile));

        Post post = Post.builder().build();

        given(postRepository.searchPostById(anyLong())).willReturn(Optional.ofNullable(post));

        Assertions.assertDoesNotThrow(() -> {
            stompService.postAlarm(postInfo);
        });
    }

    @Test
    @DisplayName("포스트 알림 실패 테스트")
    void postAlarmTest_fail() {
        Map<String, Object> postInfo = new HashMap<>();

        postInfo.put("providerId", 1);
        postInfo.put("postId", 1);

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            stompService.postAlarm(postInfo);
        });

        Assertions.assertThrows(PostNotFoundException.class, () -> {
            Profile profile = Profile.builder().build();

            given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(profile));

            stompService.postAlarm(postInfo);
        });
    }

    @Test
    @DisplayName("포스트 알림 취소 성공 테스트")
    void postAlarmCancelTest_success() {
        Map<String, Object> postInfo = new HashMap<>();

        postInfo.put("providerId", 1);
        postInfo.put("postId", 1);

        List<Notification> notificationList = new ArrayList<>();

        Notification notification = mock(Notification.class);

        Profile profile = mock(Profile.class);

        when(notification.getProfile()).thenReturn(profile);

        notificationList.add(notification);

        given(notificationRepository.searchNotificationListByProviderIdAndPostIdAndType(anyLong(), anyLong(), anyString())).willReturn(notificationList);

        Assertions.assertDoesNotThrow(() -> {
            stompService.postAlarmCancel(postInfo);
        });
    }

    @Test
    @DisplayName("포스트 알림 취소 실패 테스트")
    void postAlarmCancelTest_fail() {
        Map<String, Object> postInfo = new HashMap<>();

        postInfo.put("providerId", 1);
        postInfo.put("postId", 1);

        List<Notification> notificationList = new ArrayList<>();

        Notification notification = mock(Notification.class);

        notificationList.add(notification);

        given(notificationRepository.searchNotificationListByProviderIdAndPostIdAndType(anyLong(), anyLong(), anyString())).willReturn(notificationList);

        Assertions.assertThrows(NullPointerException.class, () -> {
            stompService.postAlarmCancel(postInfo);
        });
    }

    @Test
    @DisplayName("포스트 추천 알림 성공 테스트")
    void postRecommendTest_success() {
        Map<String, Object> postInfo = new HashMap<>();

        postInfo.put("profileId", 1);
        postInfo.put("providerId", 1);
        postInfo.put("postId", 1);

        Profile profile = Profile.builder().build();

        given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(profile));

        Profile provider = Profile.builder().build();

        given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(provider));

        Post post = Post.builder().build();

        given(postRepository.searchPostById(anyLong())).willReturn(Optional.ofNullable(post));

        Notification saveNotification = mock(Notification.class);

        given(notificationRepository.save(any())).willReturn(saveNotification);

        //stompEventListener.registerBrowserSession("userId", 1L);

        Assertions.assertDoesNotThrow(() -> {
            stompService.postRecommend(postInfo);
        });
    }

    @Test
    @DisplayName("포스트 추천 알림 실패 테스트")
    void postRecommendTest_fail() {
        Map<String, Object> postInfo = new HashMap<>();

        postInfo.put("profileId", 1);
        postInfo.put("providerId", 1);
        postInfo.put("postId", 1);

        //stompEventListener.registerBrowserSession("userId", 1L);

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            stompService.postRecommend(postInfo);
        });

        Assertions.assertThrows(PostNotFoundException.class, () -> {
            Profile profile = Profile.builder().build();

            given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(profile));

            Profile provider = Profile.builder().build();

            given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(provider));

            stompService.postRecommend(postInfo);
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
            Profile profile = Profile.builder().build();

            given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(profile));

            Profile provider = Profile.builder().build();

            given(profileRepository.findById(anyLong())).willReturn(Optional.ofNullable(provider));

            Post post = Post.builder().build();

            given(postRepository.searchPostById(anyLong())).willReturn(Optional.ofNullable(post));

            stompService.postRecommend(postInfo);
        });
    }

    @Test
    @DisplayName("포스트 추천 취소 성공 테스트")
    void postRecommendCancelTest_success() {
        Map<String, Object> postInfo = new HashMap<>();

        postInfo.put("profileId", 1);
        postInfo.put("providerId", 1);
        postInfo.put("postId", 1);

        String type = "post_recommend";

        Notification notification = Notification.builder().build();

        given(notificationRepository.searchNotificationByProfileIdAndProviderIdAndPostIdAndType(anyLong(), anyLong(), anyLong(), anyString())).willReturn(notification);

        Assertions.assertDoesNotThrow(() -> {
            stompService.postRecommendCancel(postInfo);
        });
    }

    @Test
    @DisplayName("포스트 추천 취소 실패 테스트")
    void postRecommendCancelTest_fail() {
        Map<String, Object> postInfo = new HashMap<>();

        postInfo.put("profileId", 1);
        postInfo.put("providerId", 1);
        postInfo.put("postId", 1);

        String type = "post_recommend";

        given(notificationRepository.searchNotificationByProfileIdAndProviderIdAndPostIdAndType(anyLong(), anyLong(), anyLong(), anyString())).willReturn(null);

        Assertions.assertThrows(NotificationNotFoundException.class, () -> {
            stompService.postRecommendCancel(postInfo);
        });
    }

}
