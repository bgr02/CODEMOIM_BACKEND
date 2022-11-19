package com.sideproject.codemoim.exception;

import org.springframework.security.core.AuthenticationException;

public class DuplicateLoginException extends AuthenticationException {

    public DuplicateLoginException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public DuplicateLoginException(String msg) {
        super(msg);
    }

}
