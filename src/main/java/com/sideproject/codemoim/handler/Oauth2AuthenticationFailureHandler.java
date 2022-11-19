package com.sideproject.codemoim.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sideproject.codemoim.exception.JwtExpiredTokenException;
import com.sideproject.codemoim.exception.MultipleLoginException;
import com.sideproject.codemoim.exception.Oauth2AuthenticationErrorException;
import com.sideproject.codemoim.exception.WithdrawalAccountException;
import com.sideproject.codemoim.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class Oauth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setCharacterEncoding("UTF-8");

//        String msg = "OAUTH_UNAUTHORIZED";

        if(exception instanceof InternalAuthenticationServiceException) {
            exceptionMessagePrintFormatter(exception, "InternalAuthenticationServiceException");
        }
        else if(exception instanceof BadCredentialsException) {
            exceptionMessagePrintFormatter(exception, "BadCredentialsException");
//            msg = "INVALID_PASSWORD";
        } else if(exception instanceof Oauth2AuthenticationErrorException) {
            exceptionMessagePrintFormatter(exception, "Oauth2AuthenticationErrorException");
//            msg = "INVALID_PROVIDER";
        } else if(exception instanceof WithdrawalAccountException) {
            exceptionMessagePrintFormatter(exception, "WithdrawalAccountException");
//            msg = "WITHDRAWAL_ACCOUNT";
        } else if(exception instanceof JwtExpiredTokenException) {
            exceptionMessagePrintFormatter(exception, "JwtExpiredTokenException");
//            msg = "EXPIRED_TOKEN";
        } else if(exception instanceof MultipleLoginException) {
            exceptionMessagePrintFormatter(exception, "MultipleLoginException");
//            msg = "MULTIPLE_LOGIN_ERROR";
        } else {
            exceptionMessagePrintFormatter(exception, "Oauth AuthenticationException");
        }

//        objectMapper.writeValue(response.getWriter(), msg);

        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private void exceptionMessagePrintFormatter(Exception exception, String errorName) {
        StackTraceElement[] ste = exception.getStackTrace();

        String className = ste[0].getClassName();
        String methodName = ste[0].getMethodName();
        int lineNumber = ste[0].getLineNumber();
        String fileName = ste[0].getFileName();

        log.error(errorName + ": " + exception.getMessage());
        log.error("[Class] => " + className + ", [Method] => " + methodName + ", [File] => " + fileName + ", [Line] => " + lineNumber);
    }

}
