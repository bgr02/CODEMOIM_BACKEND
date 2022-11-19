package com.sideproject.codemoim.dto;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.Cookie;
import java.util.Collection;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Cookie[] cookies;

    public JwtAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.cookies = null;
        this.setAuthenticated(true);
        this.eraseCredentials();
    }

    public JwtAuthenticationToken(Cookie[] cookies) {
        super(null);
        this.cookies = cookies;
        this.setAuthenticated(false);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
