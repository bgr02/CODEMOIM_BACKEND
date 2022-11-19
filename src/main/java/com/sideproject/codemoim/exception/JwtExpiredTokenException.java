package com.sideproject.codemoim.exception;

import org.springframework.security.core.AuthenticationException;

public class JwtExpiredTokenException extends AuthenticationException {

    public JwtExpiredTokenException(String explanation) {
        super(explanation);
    }

    public JwtExpiredTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
