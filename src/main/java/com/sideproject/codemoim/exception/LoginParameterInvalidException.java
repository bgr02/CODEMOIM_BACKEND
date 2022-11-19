package com.sideproject.codemoim.exception;

import org.springframework.security.core.AuthenticationException;

public class LoginParameterInvalidException extends AuthenticationException {

    public LoginParameterInvalidException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public LoginParameterInvalidException(String msg) {
        super(msg);
    }

}
