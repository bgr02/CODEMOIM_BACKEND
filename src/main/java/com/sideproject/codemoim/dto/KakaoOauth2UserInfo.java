package com.sideproject.codemoim.dto;

import lombok.Builder;

import java.util.HashMap;
import java.util.Map;

public class KakaoOauth2UserInfo extends Oauth2UserInfo {
    @Builder
    public KakaoOauth2UserInfo(String registrationId, Map<String, Object> attributes) {
        super(registrationId, attributes);
    }

    HashMap<String, Object> accountInfo = (HashMap<String, Object>) attributes.get("kakao_account");
    HashMap<String, String> profileInfo = (HashMap<String, String>) accountInfo.get("profile");

    @Override
    public String getPlatformUserId() {
        return ((Integer) attributes.get("id")).toString();
    }

    @Override
    public String getNickname() {
        return profileInfo.get("nickname");
    }

    @Override
    public String getProfileImgUrl() {
        return profileInfo.get("profile_image_url");
    }
}