package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.domain.Tag;
import com.sideproject.codemoim.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomProfileRepository {
    ProfileDto searchProfileDtoByUserId(Long userId);
    Profile searchProfileByUserId(Long userId);
    Profile duplicateCheckUsername(String username);
    boolean followTagExist(Long id);
    Profile searchScrapByProfileAndPost(long profileId, long postId);
    List<Tag> searchProfileFollowTags(Long id);
    List<ProfileDto> searchProfileRank();
    boolean validateProfile(Long id);
    ProfileDetailInfoDto searchProfileInfo(Long id);
    Profile searchProfileById(Long id);
    boolean searchFollowTagExist(Long profileId, Long tagId);
    Page<FollowRelationDto> searchFollowingInfo(Pageable pageable, Long id);
    Page<FollowRelationDto> searchFollowerInfo(Pageable pageable, Long id);
}
