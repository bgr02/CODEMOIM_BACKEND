package com.sideproject.codemoim.domain;

import com.sideproject.codemoim.dto.Oauth2UserInfo;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OauthInfo {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "oauth_info_id")
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false)
    private String platformUserId;
    @Column(unique = true)
    private String nickname;
    @Column
    private String profileImgUrl;
    @Column(nullable = false)
    private String provider;

    @Builder
    public OauthInfo(User user, String platformUserId, String nickname, String profileImgUrl, String provider) {
        this.user = user;
        this.platformUserId = platformUserId;
        this.nickname = nickname;
        this.profileImgUrl = profileImgUrl;
        this.provider = provider;
    }

    public OauthInfo updateOauthInfo(Oauth2UserInfo oauth2UserInfo) {
        this.platformUserId = oauth2UserInfo.getPlatformUserId();
        this.nickname = oauth2UserInfo.getNickname();
        this.profileImgUrl = oauth2UserInfo.getProfileImgUrl();

        return this;
    }
}
