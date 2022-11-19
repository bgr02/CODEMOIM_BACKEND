package com.sideproject.codemoim.exception;

import org.springframework.security.core.AuthenticationException;

public class Oauth2AuthenticationErrorException extends AuthenticationException {

    public Oauth2AuthenticationErrorException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public Oauth2AuthenticationErrorException(String msg) {
        super(msg);
    }

}
