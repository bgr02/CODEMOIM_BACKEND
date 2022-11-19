package com.sideproject.codemoim.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class NotificationDto {
    private long id;
    private String content;
    private Boolean read;
    private LocalDateTime createdDate;
}
