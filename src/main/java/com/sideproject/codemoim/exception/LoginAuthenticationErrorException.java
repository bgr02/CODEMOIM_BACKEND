package com.sideproject.codemoim.exception;

import org.springframework.security.core.AuthenticationException;

public class LoginAuthenticationErrorException extends AuthenticationException {

    public LoginAuthenticationErrorException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public LoginAuthenticationErrorException(String msg) {
        super(msg);
    }

}
