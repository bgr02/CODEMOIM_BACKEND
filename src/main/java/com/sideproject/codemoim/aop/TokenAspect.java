package com.sideproject.codemoim.aop;

import com.sideproject.codemoim.exception.BadRequestException;
import com.sideproject.codemoim.exception.JwtAuthenticationException;
import com.sideproject.codemoim.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class TokenAspect {

    private final JwtUtil jwtUtil;

    @Around("@annotation(com.sideproject.codemoim.annotation.AccessTokenUse)")
    public Object accessTokenUse(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

        Cookie[] cookies = request.getCookies();
        Long[] loginUserId = new Long[1];

        Cookie accessTokenCookie = null;

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("access_token")) {
                accessTokenCookie = cookie;
                break;
            }
        }

        Object proceed = joinPoint;

        if(accessTokenCookie != null) {
            String accessToken = accessTokenCookie.getValue();
            Jws<Claims> claimsJws = jwtUtil.parserToken(accessToken);

            loginUserId[0] = Long.parseLong(claimsJws.getBody().getSubject());

            Object[] args = joinPoint.getArgs();

            if(args.length > 1) {
                List<Object> paramList = new ArrayList<>();

                for(Object obj: args) {
                    if(obj != null) {
                        paramList.add(obj);
                    }
                }

                paramList.add(loginUserId[0]);

                proceed = joinPoint.proceed(paramList.toArray());
            } else {
                proceed = joinPoint.proceed(loginUserId);
            }

            return proceed;
        } else {
            throw new JwtAuthenticationException("Access Token do not exist.");
        }
    }

}
