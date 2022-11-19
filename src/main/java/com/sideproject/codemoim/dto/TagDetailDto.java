package com.sideproject.codemoim.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TagDetailDto {
    private long id;
    private String name;
    private String tagImgUrl;
    private int followerCount;
    private int postCount;
}
