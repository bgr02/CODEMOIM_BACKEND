package com.sideproject.codemoim.property;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@RequiredArgsConstructor
@ConstructorBinding
@ConfigurationProperties(prefix = "custom")
public class CustomProperties {
    private final Token token;
    private final Oauth2 oauth2;
    private final CookieConfig cookieConfig;
    private final Rabbitmq rabbitmq;

    @Getter
    @RequiredArgsConstructor
    public static final class Token {
        private final String secretKey;
        private final int tokenExpiredTime;
        private final int refreshTokenExpiredTime;
    }

    @Getter
    @RequiredArgsConstructor
    public static final class Oauth2 {
//        private final KakaoAccount kakaoAccount;
//        private final NaverAccount naverAccount;
        private final List<String> authorizedRedirectUris;

//        @Getter
//        @RequiredArgsConstructor
//        public static final class KakaoAccount {
//            private final String clientId;
//            private final String clientSecret;
//        }

//        @Getter
//        @RequiredArgsConstructor
//        public static final class NaverAccount {
//            private final String clientId;
//            private final String clientSecret;
//        }
    }

    @Getter
    @RequiredArgsConstructor
    public static final class CookieConfig {
        private final String protocol;
        private final String rootDomain;
        private final String backSubDomain;
        private final String frontSubDomain;
        private final Boolean secure;
        private final Boolean httpOnly;
    }

    @Getter
    @RequiredArgsConstructor
    public static final class Rabbitmq {
        private final String host;
        private final String username;
        private final String password;
    }
}
