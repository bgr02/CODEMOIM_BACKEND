package com.sideproject.codemoim.provider;

import com.sideproject.codemoim.dto.JwtAuthenticationToken;
import com.sideproject.codemoim.exception.JwtAuthenticationException;
import com.sideproject.codemoim.exception.JwtExpiredTokenException;
import com.sideproject.codemoim.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtUtil jwtUtil;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authToken = (JwtAuthenticationToken) authentication;

        String accessToken = getAccessToken(authToken);
        Jws<Claims> claimsJws = null;

        try {
            claimsJws = jwtUtil.parserToken(accessToken);
        } catch(BadCredentialsException | JwtExpiredTokenException ex) {
            throw new JwtAuthenticationException("Token information is invalid or expired.");
        }

        List<String> roles = claimsJws.getBody().get("roles", List.class);

        List<GrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new JwtAuthenticationToken(authorities);
    }

    private String getAccessToken(JwtAuthenticationToken authToken) {
        Cookie[] cookies = authToken.getCookies();

        if (cookies != null) {
            Cookie accessTokenCookie = null;

            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("access_token")) {
                    accessTokenCookie = cookie;
                    break;
                }
            }

            if (accessTokenCookie != null) {
                return accessTokenCookie.getValue();
            } else {
                throw new JwtAuthenticationException("The assigned access token cookie does not exist.");
            }

        } else {
            throw new JwtAuthenticationException("The assigned any cookie does not exist.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }

}
