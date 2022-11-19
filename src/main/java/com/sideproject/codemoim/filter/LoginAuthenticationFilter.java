package com.sideproject.codemoim.filter;

import com.google.gson.Gson;
import com.sideproject.codemoim.dto.LoginAuthenticationToken;
import com.sideproject.codemoim.exception.JwtExpiredTokenException;
import com.sideproject.codemoim.exception.LoginParameterInvalidException;
import com.sideproject.codemoim.exception.NotSupportedMethodException;
import com.sideproject.codemoim.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class LoginAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final JwtUtil jwtUtil;
    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;

    public LoginAuthenticationFilter(String defaultFilterProcessesUrl, JwtUtil jwtUtil, AuthenticationSuccessHandler successHandler, AuthenticationFailureHandler failureHandler) {
        super(defaultFilterProcessesUrl);
        this.jwtUtil = jwtUtil;
        this.successHandler = successHandler;
        this.failureHandler = failureHandler;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if(!HttpMethod.POST.name().equals(request.getMethod())) {
            throw new NotSupportedMethodException("The method is not supported.");
        }

        Cookie[] cookies = request.getCookies();
        String loginUserId = "";

        if(cookies != null) {
            Cookie accessTokenCookie = null;

            for(Cookie cookie: cookies) {
                if(cookie.getName().equals("access_token")) {
                    accessTokenCookie = cookie;
                    break;
                }
            }

            if(accessTokenCookie != null) {
                String accessToken = accessTokenCookie.getValue();
                Jws<Claims> claimsJws = null;

                try {
                    claimsJws = jwtUtil.parserToken(accessToken);
                } catch(BadCredentialsException | JwtExpiredTokenException ex) {
                    log.info("Multiple login request filtering tasks passed.");
                }

                if(claimsJws != null) {
                    loginUserId = claimsJws.getBody().getSubject();
                }
            }
        }

        String json = IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8);
        Gson gson = new Gson();
        Map<String, Object> parameterMap = gson.fromJson(json, Map.class);

        LoginAuthenticationToken token;

        if(parameterMap.get("username") != null && parameterMap.get("password") != null) {
            token = new LoginAuthenticationToken(loginUserId, (String) parameterMap.get("username"),  (String) parameterMap.get("password"));
        } else {
            throw new LoginParameterInvalidException("The request is of a parameter type that is not valid for login requests.");
        }

        return this.getAuthenticationManager().authenticate(token);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        failureHandler.onAuthenticationFailure(request, response, failed);
    }

}
