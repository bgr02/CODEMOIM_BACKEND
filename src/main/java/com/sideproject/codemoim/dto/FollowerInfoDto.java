package com.sideproject.codemoim.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowerInfoDto {
    private long followerId;
    private String followerUsername;
    private String followerUserImgUrl;
}
