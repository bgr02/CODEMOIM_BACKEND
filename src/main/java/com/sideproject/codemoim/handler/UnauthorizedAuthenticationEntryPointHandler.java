package com.sideproject.codemoim.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sideproject.codemoim.exception.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class UnauthorizedAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setCharacterEncoding("UTF-8");

//        String msg = "INVALID_ACCESS";

        if(exception instanceof NotSupportedMethodException) {
            exceptionMessagePrintFormatter(exception, "NotSupportedMethodException");
//            msg = "NOT_SUPPORT_METHOD";
        }  else if(exception instanceof InsufficientAuthenticationException) {
            exceptionMessagePrintFormatter(exception, "InsufficientAuthenticationException");
//            msg = "INSUFFICIENT_AUTH";
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
