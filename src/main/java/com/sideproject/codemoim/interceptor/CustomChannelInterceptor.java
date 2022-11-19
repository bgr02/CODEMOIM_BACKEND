package com.sideproject.codemoim.interceptor;

import com.sideproject.codemoim.config.StompEventListener;
import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomChannelInterceptor implements ChannelInterceptor {

    private final StompEventListener stompEventListener;
    private final ProfileRepository profileRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        //final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        Map<String, Object> sessionAttributes = accessor.getSessionAttributes();

        //log.info("==================Attribute================: " + sessionAttributes);

        if(StompCommand.CONNECT == accessor.getCommand()) {
            String sessionId = accessor.getSessionId();

            //log.info("========================Channel CONNECT========================");
            //log.info("========================Channel Session ID========================: " + sessionId);

            long userId = Long.parseLong((String) sessionAttributes.get("userId"));
            Profile profile = profileRepository.searchProfileByUserId(userId);

            stompEventListener.registerBrowserSession(sessionId, profile.getId());
        }

        if(StompCommand.DISCONNECT == accessor.getCommand()) {
            //log.info("========================Channel DISCONNECT========================");
        }

        return message;
    }
}
