package com.sideproject.codemoim.exception;

import org.springframework.security.core.AuthenticationException;

public class MultipleLoginException extends AuthenticationException {

    public MultipleLoginException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public MultipleLoginException(String msg) {
        super(msg);
    }

}
