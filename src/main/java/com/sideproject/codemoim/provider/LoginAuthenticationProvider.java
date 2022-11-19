package com.sideproject.codemoim.provider;

import com.sideproject.codemoim.dto.LoginAuthenticationToken;
import com.sideproject.codemoim.exception.DuplicateLoginException;
import com.sideproject.codemoim.exception.LoginAuthenticationErrorException;
import com.sideproject.codemoim.exception.MultipleLoginException;
import com.sideproject.codemoim.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginAuthenticationProvider implements AuthenticationProvider {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if(authentication == null) {
            throw new LoginAuthenticationErrorException("A login authentication error has occurred.");
        }

        LoginAuthenticationToken authToken = (LoginAuthenticationToken) authentication;

        String userId = authToken.getUserId();
        String username = authToken.getPrincipal();
        String password = authToken.getCredentials();

        UserDetails details;

        try {
            details = customUserDetailsService.loadUserByUsername(username);

            if(!userId.equals("") && userId.equals(details.getUsername())) {
                throw new DuplicateLoginException("The user is already logged in.");
            } else if(!userId.equals("") && !userId.equals(details.getUsername())) {
                throw new MultipleLoginException("Already logged in with a different account.");
            }
        } catch(UsernameNotFoundException e) {
            throw new LoginAuthenticationErrorException(username + " is not exist.");
        }

        if(!passwordEncoder.matches(password, details.getPassword())) {
            throw new LoginAuthenticationErrorException("Passwords do not match.");
        }

        return new LoginAuthenticationToken(null, details.getUsername(), null, details.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (LoginAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
