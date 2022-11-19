package com.sideproject.codemoim.dto;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class LoginAuthenticationToken extends AbstractAuthenticationToken {

    private final String userId;
    private final String principal;
    private final String credentials;

    public LoginAuthenticationToken(String userId, String principal, String credentials) {
        super(null);
        this.userId = userId;
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false);
    }

    public LoginAuthenticationToken(String userId, String principal, String credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = userId;
        this.principal = principal;
        this.credentials = credentials;
        super.setAuthenticated(true);
    }

    @Override
    public String getCredentials() {
        return credentials;
    }

    @Override
    public String getPrincipal() {
        return principal;
    }

}
