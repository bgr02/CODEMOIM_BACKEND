package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.domain.ProfileFollow;

import java.util.List;

public interface CustomProfileFollowRepository {
    List<Profile> searchFollowerByProfileId(Long id);
    List<Profile> searchFollowingByProfileId(Long id);
    ProfileFollow searchFollowingProfile(Long profileId, Long followingId);
}
