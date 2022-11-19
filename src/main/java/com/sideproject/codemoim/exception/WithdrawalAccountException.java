package com.sideproject.codemoim.exception;

import org.springframework.security.core.AuthenticationException;

public class WithdrawalAccountException extends AuthenticationException {

    public WithdrawalAccountException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public WithdrawalAccountException(String msg) {
        super(msg);
    }

}
