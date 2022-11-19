package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.Notification;
import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.dto.NotificationResult;
import com.sideproject.codemoim.exception.BadRequestException;
import com.sideproject.codemoim.exception.ProfileNotFoundException;
import com.sideproject.codemoim.repository.NotificationRepository;
import com.sideproject.codemoim.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ProfileRepository profileRepository;

    public NotificationResult searchNotificationByUserId(Long userId) {
        NotificationResult notificationResult = new NotificationResult();

        notificationResult.setNonReadNotificationCount(notificationRepository.searchNonReadNotificationCountByUserId(userId));
        notificationResult.setNotificationDtoList(notificationRepository.searchNotificationByUserId(userId));

        return notificationResult;
    }

    @Transactional
    public void modifyNotification(Map<String, Object> notificationInfo, Long userId) {
        long profileId = (int) notificationInfo.get("profileId");
        long notificationId = (int) notificationInfo.get("notificationId");

        Optional<Profile> optionalProfile = Optional.ofNullable(profileRepository.searchProfileById(profileId));

        optionalProfile.ifPresentOrElse(profile -> {
            Long profileUserId = profile.getUser().getId();

            if (profileUserId.equals(userId)) {
                //notificationRepository.updateNotification(notificationId);

                Optional<Notification> optionalNotification = notificationRepository.findById(notificationId);

                optionalNotification.ifPresentOrElse(notification -> {
                    boolean read = notification.getRead();

                    if(!read) {
                        notification.updateRead(true);
                    }
                }, () -> {
                    throw new BadRequestException("Not Exist Notification");
                });
            } else {
                throw new BadRequestException("This is not a normal request approach.");
            }
        }, () -> {
            throw new ProfileNotFoundException("Profile Not Found");
        });
    }

}
