package com.sideproject.codemoim.dto;

import com.sideproject.codemoim.domain.Tag;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class ProfileDto {
    private long id;
    //private Long userId;
    private List<String> userRoles;
    private String username;
    private String profileImgUrl;
    private int contributionPoint;
    private List<String> followTags;
    private List<Long> followingProfiles;
    private byte userStatus;
    private String email;
}
