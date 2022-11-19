package com.sideproject.codemoim.controller;

import com.sideproject.codemoim.annotation.AccessTokenUse;
import com.sideproject.codemoim.dto.NotificationResult;
import com.sideproject.codemoim.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @AccessTokenUse
    @GetMapping("/load")
    public NotificationResult loadNotification(Long userId) {
        return notificationService.searchNotificationByUserId(userId);
    }

    @AccessTokenUse
    @PatchMapping("/modify")
    public void modifyNotification(@RequestBody Map<String, Object> notificationInfo, Long userId) {
        notificationService.modifyNotification(notificationInfo, userId);
    }

}
