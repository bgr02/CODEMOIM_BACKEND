package com.sideproject.codemoim.controller;

import com.sideproject.codemoim.annotation.AccessTokenUse;
import com.sideproject.codemoim.dto.FollowRelationDto;
import com.sideproject.codemoim.dto.ProfileDto;
import com.sideproject.codemoim.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @AccessTokenUse
    @GetMapping("/load")
    public ProfileDto loadProfile(Long userId) {
        return profileService.searchProfileByUserId(userId);
    }

    @AccessTokenUse
    @PatchMapping("/modify")
    public void modifyProfile(@RequestBody Map<String, Object> profileInfo, Long userId) {
        profileService.modifyProfile(profileInfo, userId);
    }

    @GetMapping("/duplicate-check-username")
    public boolean duplicateCheckUsername(@RequestParam(name = "username") String username) throws UnsupportedEncodingException {
        return profileService.duplicateCheckUsername(username);
    }

    @GetMapping("/rank")
    public List<ProfileDto> searchProfileRank() {
        return profileService.searchProfileRank();
    }

    @AccessTokenUse
    @PatchMapping("/follow-tag")
    public boolean followTag(@RequestBody Map<String, Object> followInfo, Long userId) {
        return profileService.followTag(followInfo, userId);
    }

    @AccessTokenUse
    @PatchMapping("/unfollow-tag")
    public boolean unfollowTag(@RequestBody Map<String, Object> followInfo, Long userId) throws UnsupportedEncodingException {
        return profileService.unfollowTag(followInfo, userId);
    }

    @GetMapping("/info")
    public Map<String, Object> searchProfileTotalInfo(@RequestParam Long id) {
        return profileService.searchProfileTotalInfo(id);
    }

    @GetMapping("/following-info")
    public Page<FollowRelationDto> searchFollowingInfo(Pageable pageable, @RequestParam Long id) {
        return profileService.searchFollowingInfo(pageable, id);
    }

    @GetMapping("/follower-info")
    public Page<FollowRelationDto> searchFollowerInfo(Pageable pageable, @RequestParam Long id) {
        return profileService.searchFollowerInfo(pageable, id);
    }

    @GetMapping("/tag-info")
    public Page<FollowRelationDto> searchTagInfo(Pageable pageable, @RequestParam Long id) {
        return profileService.searchTagInfo(pageable, id);
    }

    @AccessTokenUse
    @PostMapping("/follow-profile")
    public boolean followProfile(@RequestBody Map<String, Object> followInfo, Long userId) {
        return profileService.followProfile(followInfo, userId);
    }

    @AccessTokenUse
    @DeleteMapping("/unfollow-profile")
    public boolean unfollowProfile(@RequestBody Map<String, Object> unfollowInfo, Long userId) {
        return profileService.unfollowProfile(unfollowInfo, userId);
    }

}
