package com.sideproject.codemoim.service;

import com.sideproject.codemoim.config.StompEventListener;
import com.sideproject.codemoim.domain.Comment;
import com.sideproject.codemoim.domain.Notification;
import com.sideproject.codemoim.domain.Post;
import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.dto.NotificationDto;
import com.sideproject.codemoim.exception.CommentNotFoundException;
import com.sideproject.codemoim.exception.NotificationNotFoundException;
import com.sideproject.codemoim.exception.PostNotFoundException;
import com.sideproject.codemoim.exception.ProfileNotFoundException;
import com.sideproject.codemoim.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StompService {

    private final NotificationRepository notificationRepository;
    private final ProfileRepository profileRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final ProfileFollowRepository profileFollowRepository;
    //private final SimpMessagingTemplate simpMessagingTemplate;
    //private final StompEventListener stompEventListener;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void commentAlarm(Map<String, Object> commentInfo) {
        long profileId = (int) commentInfo.get("profileId");
        long providerId = (int) commentInfo.get("providerId");
        long postId = (int) commentInfo.get("postId");
        long commentId = (int) commentInfo.get("commentId");

        Profile profile = profileRepository.findById(profileId).orElseThrow(() -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        Profile provider = profileRepository.findById(providerId).orElseThrow(() -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            throw new CommentNotFoundException("Comment Not Found");
        });

        String content = provider.getUsername() + "님이 게시글 #" + postId + "에 댓글을 남겼습니다.";

        Notification notification = Notification.builder()
                .profile(profile)
                .provider(provider)
                .comment(comment)
                .content(content)
                .read(false)
                .type("comment_alarm")
                .build();

        Notification saveNotification = notificationRepository.save(notification);

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setId(saveNotification.getId());
        notificationDto.setContent(saveNotification.getContent());
        notificationDto.setRead(saveNotification.getRead());
        notificationDto.setCreatedDate(saveNotification.getCreatedDate());

        rabbitTemplate.convertAndSend("notification.exchange", "alarm.comment." + profileId, notificationDto);
    }

    @Transactional
    public void commentAlarmCancel(Map<String, Object> commentInfo) {
        long profileId = (int) commentInfo.get("profileId");
        boolean read = (boolean) commentInfo.get("read");
        long notificationId = (int) commentInfo.get("notificationId");

        Map<String, Object> params = new HashMap<>();

        params.put("read", read);
        params.put("notificationId", notificationId);

        rabbitTemplate.convertAndSend("notification.exchange", "alarm.comment.cancel." + profileId, params);
    }

    @Transactional
    public void commentRecommend(Map<String, Object> commentInfo) {
        long profileId = (int) commentInfo.get("profileId");
        long providerId = (int) commentInfo.get("providerId");
        long postId = (int) commentInfo.get("postId");
        long commentId = (int) commentInfo.get("commentId");

        Profile profile = profileRepository.findById(profileId).orElseThrow(() -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        Profile provider = profileRepository.findById(providerId).orElseThrow(() -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> {
            throw new CommentNotFoundException("Comment Not Found");
        });

        String content = provider.getUsername() + "님이 게시글 #" + postId + "에 작성한 댓글을 추천하였습니다.";

        Notification notification = Notification.builder()
                .profile(profile)
                .provider(provider)
                .comment(comment)
                .content(content)
                .read(false)
                .type("comment_recommend")
                .build();

        Notification saveNotification = notificationRepository.save(notification);

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setId(saveNotification.getId());
        notificationDto.setContent(saveNotification.getContent());
        notificationDto.setRead(saveNotification.getRead());
        notificationDto.setCreatedDate(saveNotification.getCreatedDate());

        rabbitTemplate.convertAndSend("notification.exchange", "comment.recommend." + profileId, notificationDto);
    }

    @Transactional
    public void commentRecommendCancel(Map<String, Object> commentInfo) {
        long profileId = (int) commentInfo.get("profileId");
        long providerId = (int) commentInfo.get("providerId");
        long commentId = (int) commentInfo.get("commentId");
        String type = "comment_recommend";

        Notification notification = notificationRepository.searchNotificationByProfileIdAndProviderIdAndCommentIdAndType(profileId, providerId, commentId, type);

        if(notification != null) {
            notificationRepository.delete(notification);

            Map<String, Object> params = new HashMap<>();

            params.put("read", notification.getRead());
            params.put("notificationId", notification.getId());

            rabbitTemplate.convertAndSend("notification.exchange", "comment.recommend.cancel." + profileId, params);
        } else {
            throw new NotificationNotFoundException("Notification Not Found");
        }
    }

    @Transactional
    public void postAlarm(Map<String, Object> postInfo) {
        long providerId = (int) postInfo.get("providerId");
        long postId = (int) postInfo.get("postId");

        Profile provider = profileRepository.findById(providerId).orElseThrow(() -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        Post post = postRepository.searchPostById(postId).orElseThrow(() -> {
            throw new PostNotFoundException("Post Not Found");
        });

        List<Profile> profileList = profileFollowRepository.searchFollowerByProfileId(providerId);

        if(!profileList.isEmpty()) {
            for (Profile profile : profileList) {
                String content = "회원님이 팔로잉한 " + provider.getUsername() + "님이 게시글 #" + postId + "을 작성하였습니다.";

                Notification notification = Notification.builder()
                        .profile(profile)
                        .provider(provider)
                        .post(post)
                        .content(content)
                        .read(false)
                        .type("post_alarm")
                        .build();

                Notification saveNotification = notificationRepository.save(notification);

                NotificationDto notificationDto = new NotificationDto();
                notificationDto.setId(saveNotification.getId());
                notificationDto.setContent(saveNotification.getContent());
                notificationDto.setRead(saveNotification.getRead());
                notificationDto.setCreatedDate(saveNotification.getCreatedDate());

                rabbitTemplate.convertAndSend("notification.exchange", "alarm.post." + profile.getId(), notificationDto);
            }
        }
    }

    @Transactional
    public void postAlarmCancel(Map<String, Object> postInfo) {
        long providerId = (int) postInfo.get("providerId");
        long postId = (int) postInfo.get("postId");

        List<Notification> notificationList = notificationRepository.searchNotificationListByProviderIdAndPostIdAndType(providerId, postId, "post_alarm");

        if(!notificationList.isEmpty()) {
            for (Notification notification : notificationList) {
                notificationRepository.delete(notification);

                Map<String, Object> params = new HashMap<>();

                params.put("read", notification.getRead());
                params.put("notificationId", notification.getId());
                params.put("postId", postId);

                rabbitTemplate.convertAndSend("notification.exchange", "alarm.post.cancel." + notification.getProfile().getId(), params);
            }
        }
    }

    @Transactional
    public void postRecommend(Map<String, Object> postInfo) {
        long profileId = (int) postInfo.get("profileId");
        long providerId = (int) postInfo.get("providerId");
        long postId = (int) postInfo.get("postId");

        Profile profile = profileRepository.findById(profileId).orElseThrow(() -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        Profile provider = profileRepository.findById(providerId).orElseThrow(() -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });

        Post post = postRepository.searchPostById(postId).orElseThrow(() -> {
            throw new PostNotFoundException("Post Not Found");
        });

        String content = provider.getUsername() + "님이 게시글 #" + postId + "을 추천하였습니다.";

        Notification notification = Notification.builder()
                .profile(profile)
                .provider(provider)
                .post(post)
                .content(content)
                .read(false)
                .type("post_recommend")
                .build();

        Notification saveNotification = notificationRepository.save(notification);

        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setId(saveNotification.getId());
        notificationDto.setContent(saveNotification.getContent());
        notificationDto.setRead(saveNotification.getRead());
        notificationDto.setCreatedDate(saveNotification.getCreatedDate());

        rabbitTemplate.convertAndSend("notification.exchange", "post.recommend." + profileId, notificationDto);
    }

    @Transactional
    public void postRecommendCancel(Map<String, Object> postInfo) {
        long profileId = (int) postInfo.get("profileId");
        long providerId = (int) postInfo.get("providerId");
        long postId = (int) postInfo.get("postId");
        String type = "post_recommend";

        Notification notification = notificationRepository.searchNotificationByProfileIdAndProviderIdAndPostIdAndType(profileId, providerId, postId, type);

        if(notification != null) {
            notificationRepository.delete(notification);

            Map<String, Object> params = new HashMap<>();

            params.put("read", notification.getRead());
            params.put("notificationId", notification.getId());

            rabbitTemplate.convertAndSend("notification.exchange", "post.recommend.cancel." + profileId, params);
        } else {
            throw new NotificationNotFoundException("Notification Not Found");
        }
    }

}
