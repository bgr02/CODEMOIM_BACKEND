package com.sideproject.codemoim.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class CustomOauth2User implements OAuth2User {

    private String id;
    private String provider;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public CustomOauth2User(String userId, String registrationId, Collection<? extends GrantedAuthority> authorities, Map<String, Object> attributes) {
        this.id = userId;
        this.provider = registrationId;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return id;
    }

    public String getProvider() {
        return provider;
    }
}
