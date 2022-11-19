package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Comment;
import com.sideproject.codemoim.domain.Notification;
import com.sideproject.codemoim.domain.Post;
import com.sideproject.codemoim.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long>, CustomNotificationRepository {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Notification n where n.post = :post and n.type = :type")
    int deleteNotificationByPostAndType(@Param("post") Post post, @Param("type") String type);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Notification n where n.comment = :comment and n.type = :type")
    int deleteNotificationByCommentAndType(@Param("comment") Comment comment, @Param("type") String type);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Notification n where n.profile = :profile and n.type = :type")
    int deleteNotificationByProfileAndType(@Param("profile") Profile profile, @Param("type") String type);

}
