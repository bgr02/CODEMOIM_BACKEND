package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.Notification;
import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.domain.User;
import com.sideproject.codemoim.dto.NotificationDto;
import com.sideproject.codemoim.dto.NotificationResult;
import com.sideproject.codemoim.exception.BadRequestException;
import com.sideproject.codemoim.exception.ProfileNotFoundException;
import com.sideproject.codemoim.repository.NotificationRepository;
import com.sideproject.codemoim.repository.ProfileRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    NotificationService notificationService;

    @Mock
    NotificationRepository notificationRepository;

    @Mock
    ProfileRepository profileRepository;

    @Test
    @DisplayName("사용자 알림 검색 성공 테스트")
    void searchNotificationByUserIdTest_success() {
        List<NotificationDto> notificationDtoList = new ArrayList<>();

        given(notificationRepository.searchNonReadNotificationCountByUserId(anyLong())).willReturn(1L);
        given(notificationRepository.searchNotificationByUserId(anyLong())).willReturn(notificationDtoList);

        NotificationResult notificationResult = notificationService.searchNotificationByUserId(anyLong());

        Assertions.assertNotEquals(notificationResult.getNonReadNotificationCount(), 0);
    }

    @Test
    @DisplayName("사용자 알림 검색 실패 테스트")
    void searchNotificationByUserIdTest_fail() {
        List<NotificationDto> notificationDtoList = new ArrayList<>();

        given(notificationRepository.searchNonReadNotificationCountByUserId(anyLong())).willReturn(0L);
        given(notificationRepository.searchNotificationByUserId(anyLong())).willReturn(null);

        NotificationResult notificationResult = notificationService.searchNotificationByUserId(anyLong());

        Assertions.assertEquals(notificationResult.getNotificationDtoList(), null);
    }

    @Test
    @DisplayName("알림 읽음 처리 성공 테스트")
    void modifyNotificationTest_success() {
        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        Notification notification = Notification
                .builder()
                .read(false)
                .build();

        given(notificationRepository.findById(anyLong())).willReturn(Optional.ofNullable(notification));

        Map<String, Object> notificationInfo = new HashMap<>();
        notificationInfo.put("profileId", 1);
        notificationInfo.put("notificationId", 1);

        Assertions.assertDoesNotThrow(() -> {
            notificationService.modifyNotification(notificationInfo, anyLong());
        });
    }

    @Test
    @DisplayName("알림 읽음 처리 실패 테스트")
    void modifyNotificationTest_fail() {
        Notification notification = Notification
                .builder()
                .build();

        given(notificationRepository.findById(anyLong())).willReturn(Optional.ofNullable(null));

        Map<String, Object> notificationInfo = new HashMap<>();
        notificationInfo.put("profileId", 1);
        notificationInfo.put("notificationId", 1);

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            notificationService.modifyNotification(notificationInfo, anyLong());
        });

        Assertions.assertThrows(BadRequestException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            notificationService.modifyNotification(notificationInfo, anyLong());
        });
    }

}