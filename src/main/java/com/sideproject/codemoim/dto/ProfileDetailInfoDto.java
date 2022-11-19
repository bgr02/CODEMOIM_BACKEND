package com.sideproject.codemoim.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDetailInfoDto {
    private long id;
    private String username;
    private String profileImgUrl;
    private int contributionPoint;
    private int followingCount;
    private int followerCount;
    private int tagCount;
    private int postCount;
    private int commentCount;
    private int scrapCount;
}
