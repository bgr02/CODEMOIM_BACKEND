package com.sideproject.codemoim.dto;

import lombok.Builder;

import java.util.Map;

public class GithubOauth2UserInfo extends Oauth2UserInfo {
    @Builder
    public GithubOauth2UserInfo(String registrationId, Map<String, Object> attributes) {
        super(registrationId, attributes);
    }

    @Override
    public String getPlatformUserId() {
        return ((Integer) attributes.get("id")).toString();
    }

    @Override
    public String getNickname() {
        return (String) attributes.get("login");
    }

    @Override
    public String getProfileImgUrl() {
        return (String) attributes.get("avatar_url");
    }
}
