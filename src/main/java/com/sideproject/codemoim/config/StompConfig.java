package com.sideproject.codemoim.config;

import com.sideproject.codemoim.interceptor.CustomChannelInterceptor;
import com.sideproject.codemoim.interceptor.HttpHandshakeInterceptor;
import com.sideproject.codemoim.property.CustomProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class StompConfig implements WebSocketMessageBrokerConfigurer {

    private final HttpHandshakeInterceptor httpHandshakeInterceptor;
    private final CustomChannelInterceptor customChannelInterceptor;
    private final CustomProperties customProperties;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/stomp")
                //.addInterceptors(httpHandshakeInterceptor)
                .setAllowedOrigins(customProperties.getCookieConfig().getProtocol() + "://" + customProperties.getCookieConfig().getFrontSubDomain())
                .withSockJS()
                .setHeartbeatTime(25000);
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/broker");

        registry.enableStompBrokerRelay("/exchange", "/queue", "/topic", "/amq/queue", "/temp-queue/")
                .setRelayHost(customProperties.getRabbitmq().getHost())
                .setRelayPort(5675)
                .setClientLogin(customProperties.getRabbitmq().getUsername())
                .setClientPasscode(customProperties.getRabbitmq().getPassword())
                .setSystemLogin(customProperties.getRabbitmq().getUsername())
                .setSystemPasscode(customProperties.getRabbitmq().getPassword())
                .setSystemHeartbeatSendInterval(10000)
                .setSystemHeartbeatReceiveInterval(10000);
    }

//    @Override
//    public void configureClientInboundChannel(ChannelRegistration registration) {
//        registration.interceptors(customChannelInterceptor);
//    }
}
