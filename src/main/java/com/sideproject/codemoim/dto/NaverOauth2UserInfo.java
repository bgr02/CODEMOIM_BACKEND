package com.sideproject.codemoim.dto;

import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

public class NaverOauth2UserInfo extends Oauth2UserInfo {
    @Builder
    public NaverOauth2UserInfo(String registrationId, Map<String, Object> attributes) {
        super(registrationId, attributes);
    }

    @Override
    public String getPlatformUserId() {
        return (String) attributes.get("id");
    }

    @Override
    public String getNickname() {
        return (String) attributes.get("nickname");
    }

    @Override
    public String getProfileImgUrl() {
        return (String) attributes.get("profile_image");
    }
}