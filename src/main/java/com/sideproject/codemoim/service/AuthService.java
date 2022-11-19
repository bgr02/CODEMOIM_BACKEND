package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.Token;
import com.sideproject.codemoim.domain.User;
import com.sideproject.codemoim.exception.BadRequestException;
import com.sideproject.codemoim.exception.JwtAuthenticationException;
import com.sideproject.codemoim.exception.JwtExpiredTokenException;
import com.sideproject.codemoim.exception.ReissueAccessTokenErrorException;
import com.sideproject.codemoim.property.CustomProperties;
import com.sideproject.codemoim.repository.UserRepository;
import com.sideproject.codemoim.util.CookieUtils;
import com.sideproject.codemoim.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final TokenService tokenService;
    private final CustomProperties customProperties;
    private final UserRepository userRepository;

    public void reissueAccessToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getRefreshToken(request);

        Jws<Claims> claimsJws = null;

        try {
            claimsJws = jwtUtil.parserToken(refreshToken);
        } catch (BadCredentialsException e) {
            throw new ReissueAccessTokenErrorException("An error occurred due to the use of invalid Refresh token.");
        } catch (JwtExpiredTokenException e) {
            throw new ReissueAccessTokenErrorException("An error occurred due to the use of Refresh token whose expiration date has expired.");
        }

        String userId = claimsJws.getBody().getSubject();
        String provider = (String) claimsJws.getBody().get("provider");
        LocalDateTime expiredDate = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());

        boolean tokenRefreshTokenFlag = tokenService.searchRefreshToken(Long.parseLong(userId), refreshToken, expiredDate, provider);

        if(tokenRefreshTokenFlag) {
            Optional<User> optionalUser = userRepository.searchUserByIdAndStatus(Long.parseLong(userId));

            User user = optionalUser.orElseThrow(() -> {
                throw new ReissueAccessTokenErrorException("Invalid User.");
            });

            List<SimpleGrantedAuthority> authorities = user.getRoles()
                    .stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName().toString()))
                    .collect(Collectors.toList());

            String accessToken = jwtUtil.createAccessToken(userId, provider, authorities);

            String accessTokenCookie = CookieUtils.createTokenCookie("access_token", customProperties.getCookieConfig().getRootDomain(), accessToken, customProperties.getToken().getTokenExpiredTime(), customProperties.getCookieConfig().getHttpOnly(), customProperties.getCookieConfig().getSecure());

            response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie);
        } else {
            throw new ReissueAccessTokenErrorException("Invalid Refresh token.");
        }
    }

    private String getRefreshToken(HttpServletRequest request) {
        Optional<Cookie[]> optionalCookies = Optional.ofNullable(request.getCookies());

        Cookie[] cookies = optionalCookies.orElseThrow(() -> {
            throw new ReissueAccessTokenErrorException("There is a problem with the request cookie.");
        });

        List<Cookie> cookieList = Arrays
                .stream(cookies)
                .filter(cookie -> cookie.getName().equals("refresh_token"))
                .collect(Collectors.toList());

        Cookie refreshTokenCookie;

        if (!cookieList.isEmpty()) {
            refreshTokenCookie = cookieList.get(0);

            if(refreshTokenCookie.getValue() != null) {
                return refreshTokenCookie.getValue();
            } else {
                throw new ReissueAccessTokenErrorException("Refresh token cookies do not exist.");
            }
        } else {
            throw new ReissueAccessTokenErrorException("Refresh token cookies do not exist.");
        }
    }

    public void clearCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();

                if (name.equals("access_token") || name.equals("refresh_token")) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public boolean validateAuth(HttpServletRequest request) {
        Optional<Cookie[]> optionalCookies = Optional.ofNullable(request.getCookies());

        Cookie[] cookies = optionalCookies.orElseThrow(() -> {
            throw new BadRequestException("There is a problem with the request cookie.");
        });

        Cookie accessTokenCookie = null;
        Cookie refreshTokenCookie = null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("access_token")) {
                accessTokenCookie = cookie;
            }

            if (cookie.getName().equals("refresh_token")) {
                refreshTokenCookie = cookie;
            }
        }

        if (accessTokenCookie != null) {
            String accessToken = accessTokenCookie.getValue();
            Jws<Claims> claimsJws = null;

            try {
                claimsJws = jwtUtil.parserToken(accessToken);
            }
//            catch (BadCredentialsException exception) {
//                throw new JwtAuthenticationException("An error occurred due to the use of invalid Access token.");
//            } catch (JwtExpiredTokenException e) {
//                throw new JwtAuthenticationException("An error occurred due to the use of Access token whose expiration date has expired.");
//                //return false;
//            }
            catch (Exception exception) {
                StackTraceElement[] ste = exception.getStackTrace();

                String className = ste[0].getClassName();
                String methodName = ste[0].getMethodName();
                int lineNumber = ste[0].getLineNumber();
                String fileName = ste[0].getFileName();

                log.error("MessagingException: " + exception.getMessage());
                log.error("[Class] => " + className + ", [Method] => " + methodName + ", [File] => " + fileName + ", [Line] => " + lineNumber);

                return false;
            }

            return claimsJws != null;
        } else {
            if(refreshTokenCookie != null) {
                String refreshToken = refreshTokenCookie.getValue();
                Jws<Claims> claimsJws = null;

                try {
                    claimsJws = jwtUtil.parserToken(refreshToken);
                } catch (Exception exception) {
                    StackTraceElement[] ste = exception.getStackTrace();

                    String className = ste[0].getClassName();
                    String methodName = ste[0].getMethodName();
                    int lineNumber = ste[0].getLineNumber();
                    String fileName = ste[0].getFileName();

                    log.error("MessagingException: " + exception.getMessage());
                    log.error("[Class] => " + className + ", [Method] => " + methodName + ", [File] => " + fileName + ", [Line] => " + lineNumber);

                    return false;
                }

                return claimsJws != null;
            } else {
                //throw new JwtAuthenticationException("The assigned Refresh token cookie does not exist.");
                return false;
            }
        }
    }

}
