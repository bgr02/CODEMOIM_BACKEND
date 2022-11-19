package com.sideproject.codemoim.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

public abstract class Oauth2UserInfo {
    protected String provider;
    protected Map<String, Object> attributes;

    public Oauth2UserInfo(String registrationId, Map<String, Object> attributes) {
        this.provider = registrationId;
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public String getProvider() {
        return provider;
    }

    public abstract String getPlatformUserId();
    public abstract String getNickname();
    public abstract String getProfileImgUrl();
}
