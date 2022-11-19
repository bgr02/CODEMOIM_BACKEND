package com.sideproject.codemoim.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class StompEventListener {
    private static ConcurrentMap<String, Object> browserSessionMap = new ConcurrentHashMap<>();

    /**
     * Handle session connected events. * * @param event the event
     */
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        //StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        //log.info("Received a new web socket connection. Session ID : [{}]", headerAccessor.getSessionId());
        //log.info("======================Connect Event=======================: " + headerAccessor.getSessionAttributes());
    }

    public void registerBrowserSession(String sessionId, Long userId) {
        browserSessionMap.put(sessionId, userId);
    }

    /**
     * Handle session disconnected events. * * @param event the event
     */
    @EventListener
    void handleWebSocketDisConnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        //log.info("===================Before Delete User Info================: " + browserSessionMap);
        //log.info("===================Session Id================: " + headerAccessor.getSessionId());

        browserSessionMap.remove(headerAccessor.getSessionId());

        //log.info("===================After Delete User Info================: "+browserSessionMap);
    }

    public boolean validateUser(long profileId) {
        if(!browserSessionMap.isEmpty()) {
            return browserSessionMap.containsValue(profileId);
        } else {
            return false;
        }
    }
}