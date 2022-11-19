package com.sideproject.codemoim.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sideproject.codemoim.domain.Token;
import com.sideproject.codemoim.domain.User;
import com.sideproject.codemoim.property.CustomProperties;
import com.sideproject.codemoim.repository.TokenRepository;
import com.sideproject.codemoim.repository.UserRepository;
import com.sideproject.codemoim.service.TokenService;
import com.sideproject.codemoim.service.UserService;
import com.sideproject.codemoim.util.CookieUtils;
import com.sideproject.codemoim.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Transactional
public class LoginAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final TokenService tokenService;
    private final CustomProperties customProperties;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String userId = (String) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        Optional<User> optionalUser = userRepository.searchUserByIdAndStatus(Long.parseLong(userId));

        User user = optionalUser.orElseThrow(() -> {
            throw new UsernameNotFoundException("Custom User Not Exist");
        });

        tokenService.deleteTokenByUserAndExpiredDate(user);

        String accessToken = jwtUtil.createAccessToken(userId, "local", authorities);
        String refreshToken = jwtUtil.createRefreshToken(userId, "local", authorities);

        Token token = Token.builder()
                .user(user)
                .refreshToken(refreshToken)
                .expiredDate(LocalDateTime.now().plusSeconds(customProperties.getToken().getRefreshTokenExpiredTime() / 1000))
                .provider("local")
                .build();

        tokenService.createToken(token);

        String accessTokenCookie = CookieUtils.createTokenCookie("access_token", customProperties.getCookieConfig().getRootDomain(), accessToken, customProperties.getToken().getTokenExpiredTime(), customProperties.getCookieConfig().getHttpOnly(), customProperties.getCookieConfig().getSecure());
        String refreshTokenCookie = CookieUtils.createTokenCookie("refresh_token", customProperties.getCookieConfig().getRootDomain(), refreshToken, customProperties.getToken().getRefreshTokenExpiredTime(), customProperties.getCookieConfig().getHttpOnly(), customProperties.getCookieConfig().getSecure());

        String loginStatusCookie = ResponseCookie.from("login_status", "loggingIn")
                .domain(customProperties.getCookieConfig().getRootDomain())
                .path("/")
                .maxAge(-1)
                .secure(customProperties.getCookieConfig().getSecure())
                .sameSite("Lax")
                .build()
                .toString();

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie);
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie);
        response.addHeader(HttpHeaders.SET_COOKIE, loginStatusCookie);

//        Map<String, Object> resParam = new HashMap<>();
//
//        resParam.put("expired_time", customProperties.getToken().getTokenExpiredTime());
//
//        response.setStatus(HttpServletResponse.SC_OK);
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setCharacterEncoding("UTF-8");
//
//        objectMapper.writeValue(response.getWriter(), resParam);
    }

}
