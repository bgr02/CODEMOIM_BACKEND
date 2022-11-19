package com.sideproject.codemoim.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class NotificationResult {
    private long nonReadNotificationCount;
    private List<NotificationDto> notificationDtoList;
}
