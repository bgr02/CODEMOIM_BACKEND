package com.sideproject.codemoim.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.codemoim.domain.Notification;
import com.sideproject.codemoim.domain.QProfile;
import com.sideproject.codemoim.dto.NotificationDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.sideproject.codemoim.domain.QComment.comment;
import static com.sideproject.codemoim.domain.QNotification.notification;
import static com.sideproject.codemoim.domain.QPost.post;
import static com.sideproject.codemoim.domain.QProfile.profile;
import static com.sideproject.codemoim.domain.QUser.user;

@RequiredArgsConstructor
public class CustomNotificationRepositoryImpl implements CustomNotificationRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Long searchNonReadNotificationCountByUserId(Long userId) {
        return queryFactory
                .selectFrom(notification)
                .join(notification.profile, profile)
                .join(profile.user, user)
                .where(profile.user.id.eq(userId), notification.read.eq(false), user.status.ne((byte) 2))
                .fetchCount();
    }

    @Override
    public List<NotificationDto> searchNotificationByUserId(Long userId) {
        return queryFactory
                .select(Projections.bean(NotificationDto.class,
                        notification.id,
                        notification.content,
                        notification.read,
                        notification.createdDate
                ))
                .from(notification)
                .join(notification.profile, profile)
                .join(profile.user, user)
                .where(profile.user.id.eq(userId), user.status.ne((byte) 2))
                .orderBy(notification.createdDate.desc())
                .fetch();
    }

    @Override
    public Notification searchNotificationByProfileIdAndCommentIdAndType(long profileId, long commentId, String type) {
        return queryFactory
                .selectFrom(notification)
                .join(notification.profile, profile)
                .join(notification.comment, comment)
                .where(profile.id.eq(profileId), comment.id.eq(commentId), notification.type.eq(type))
                .fetchOne();
    }

    @Override
    public Notification searchNotificationByProfileIdAndProviderIdAndCommentIdAndType(long profileId, long providerId, long commentId, String type) {
        QProfile provider = new QProfile("provider");

        return queryFactory
                .selectFrom(notification)
                .join(notification.profile, profile)
                .join(notification.provider, provider)
                .join(notification.comment, comment)
                .where(profile.id.eq(profileId), provider.id.eq(providerId), comment.id.eq(commentId), notification.type.eq(type))
                .fetchOne();
    }

    @Override
    public List<Notification> searchNotificationListByProviderIdAndPostIdAndType(long providerId, long postId, String type) {
        return queryFactory
                .selectFrom(notification)
                .join(notification.provider, profile)
                .join(profile.user, user)
                .join(notification.post, post)
                .where(profile.id.eq(providerId), post.id.eq(postId), notification.type.eq(type), user.status.ne((byte) 2))
                .fetch();
    }

    @Override
    public Notification searchNotificationByProfileIdAndProviderIdAndPostIdAndType(long profileId, long providerId, long postId, String type) {
        QProfile provider = new QProfile("provider");

        return queryFactory
                .selectFrom(notification)
                .join(notification.profile, profile)
                .join(notification.provider, provider)
                .join(notification.post, post)
                .where(profile.id.eq(profileId), provider.id.eq(providerId), post.id.eq(postId), notification.type.eq(type))
                .fetchOne();
    }

}
