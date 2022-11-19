package com.sideproject.codemoim.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sideproject.codemoim.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommonAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setCharacterEncoding("UTF-8");

//        String msg = "UNAUTHORIZED";

        if (exception instanceof LoginAuthenticationErrorException) {
            exceptionMessagePrintFormatter(exception, "LoginAuthenticationErrorException");
//            msg = "INVALID_USER_INFO";
        } else if (exception instanceof LoginParameterInvalidException) {
            exceptionMessagePrintFormatter(exception, "LoginParameterInvalidException");
//            msg = "INVALID_PARAMETER";
        } else if(exception instanceof BadCredentialsException) {
            exceptionMessagePrintFormatter(exception, "BadCredentialsException");
//            msg = "INVALID_TOKEN";
        } else if(exception instanceof JwtExpiredTokenException) {
            exceptionMessagePrintFormatter(exception, "JwtExpiredTokenException");
//            msg = "TOKEN_EXPIRED";
        } else if(exception instanceof JwtAuthenticationException) {
            exceptionMessagePrintFormatter(exception, "JwtAuthenticationException");
//            msg = "TOKEN_ERROR";
        } else if(exception instanceof DuplicateLoginException) {
            exceptionMessagePrintFormatter(exception, "DuplicateLoginException");
//            msg = "DUPLICATE_USER";
        } else if(exception instanceof MultipleLoginException) {
            exceptionMessagePrintFormatter(exception, "MultipleLoginException");
//            msg = "MULTIPLE_LOGIN_ERROR";
        } else if(exception instanceof UsernameNotFoundException) {
            exceptionMessagePrintFormatter(exception, "UsernameNotFoundException");
//            msg = "USERNAME_NOT_FOUND";
        } else if(exception instanceof NotSupportedMethodException) {
            exceptionMessagePrintFormatter(exception, "NotSupportedMethodException");
//            msg = "NOT_SUPPORT_METHOD";
        } else if(exception instanceof WithdrawalAccountException) {
            exceptionMessagePrintFormatter(exception, "WithdrawalAccountException");
//            msg = "WITHDRAWAL_ACCOUNT";
        } else {
            exceptionMessagePrintFormatter(exception, "AuthenticationException");
        }

//        objectMapper.writeValue(response.getWriter(), msg);
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
