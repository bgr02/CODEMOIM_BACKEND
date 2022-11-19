package com.sideproject.codemoim.exception;

import org.springframework.security.core.AuthenticationException;

public class NotSupportedMethodException extends AuthenticationException {

    public NotSupportedMethodException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public NotSupportedMethodException(String msg) {
        super(msg);
    }

}
