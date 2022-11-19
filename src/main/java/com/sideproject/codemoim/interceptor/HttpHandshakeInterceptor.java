package com.sideproject.codemoim.interceptor;

import com.sideproject.codemoim.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.Cookie;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HttpHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;

            Cookie[] cookies = servletRequest.getServletRequest().getCookies();

            if (cookies != null) {
                Cookie accessTokenCookie = null;

                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("access_token")) {
                        accessTokenCookie = cookie;
                        break;
                    }
                }

                if (accessTokenCookie != null) {
                    String value = accessTokenCookie.getValue();

                    Jws<Claims> claimsJws = jwtUtil.parserToken(value);

                    String userId = claimsJws.getBody().getSubject();
                    String sessionId = servletRequest.getServletRequest().getSession().getId();

                    attributes.put("userId", userId);

                    //log.info("=====================Before Handshake============================");
                    //log.info("Session Id: " + sessionId);
                    //log.info("User Id: " + userId);

                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        //Nothing
    }

}
