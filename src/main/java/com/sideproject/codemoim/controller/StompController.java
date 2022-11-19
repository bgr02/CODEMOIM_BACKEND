package com.sideproject.codemoim.controller;

import com.sideproject.codemoim.config.StompEventListener;
import com.sideproject.codemoim.dto.NotificationDto;
import com.sideproject.codemoim.service.StompService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class StompController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final StompService stompService;
    private final StompEventListener stompEventListener;

    @MessageMapping("/comment/alarm")
    public void commentAlarm(Map<String, Object> commentInfo, SimpMessageHeaderAccessor messageHeaderAccessor) {
        stompService.commentAlarm(commentInfo);
    }

    @MessageMapping("/comment/alarm-cancel")
    public void commentAlarmCancel(Map<String, Object> commentInfo, SimpMessageHeaderAccessor messageHeaderAccessor) {
        stompService.commentAlarmCancel(commentInfo);
    }

    @MessageMapping("/comment/recommend")
    public void commentRecommend(Map<String, Object> commentInfo, SimpMessageHeaderAccessor messageHeaderAccessor) {
        stompService.commentRecommend(commentInfo);
    }

    @MessageMapping("/comment/recommend-cancel")
    public void commentRecommendCancel(Map<String, Object> commentInfo, SimpMessageHeaderAccessor messageHeaderAccessor) {
        stompService.commentRecommendCancel(commentInfo);
    }

    @MessageMapping("/post/alarm")
    public void postAlarm(Map<String, Object> postInfo, SimpMessageHeaderAccessor messageHeaderAccessor) {
        stompService.postAlarm(postInfo);
    }

    @MessageMapping("/post/alarm-cancel")
    public void postAlarmCancel(Map<String, Object> postInfo, SimpMessageHeaderAccessor messageHeaderAccessor) {
        stompService.postAlarmCancel(postInfo);
    }

    @MessageMapping("/post/recommend")
    public void postRecommend(Map<String, Object> postInfo, SimpMessageHeaderAccessor messageHeaderAccessor) {
        stompService.postRecommend(postInfo);
    }

    @MessageMapping("/post/recommend-cancel")
    public void postRecommendCancel(Map<String, Object> postInfo, SimpMessageHeaderAccessor messageHeaderAccessor) {
        stompService.postRecommendCancel(postInfo);
    }

}