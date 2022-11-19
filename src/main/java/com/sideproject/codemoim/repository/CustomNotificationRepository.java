package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Notification;
import com.sideproject.codemoim.dto.NotificationDto;

import java.util.List;

public interface CustomNotificationRepository {
    Long searchNonReadNotificationCountByUserId(Long userId);
    List<NotificationDto> searchNotificationByUserId(Long userId);
    Notification searchNotificationByProfileIdAndCommentIdAndType(long profileId, long commentId, String type);
    Notification searchNotificationByProfileIdAndProviderIdAndCommentIdAndType(long profileId, long providerId, long commentId, String type);
    List<Notification> searchNotificationListByProviderIdAndPostIdAndType(long providerId, long postId, String type);
    Notification searchNotificationByProfileIdAndProviderIdAndPostIdAndType(long profileId, long providerId, long postId, String type);
}
