package com.sideproject.codemoim.exception;

import org.springframework.security.web.authentication.rememberme.InvalidCookieException;

public class ReissueAccessTokenErrorException extends InvalidCookieException {

    public ReissueAccessTokenErrorException(String message) {
        super(message);
    }

}
