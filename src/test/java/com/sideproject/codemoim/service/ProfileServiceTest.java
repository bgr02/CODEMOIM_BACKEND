package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.dto.ProfileDetailInfoDto;
import com.sideproject.codemoim.dto.ProfileDto;
import com.sideproject.codemoim.exception.ProfileNotFoundException;
import com.sideproject.codemoim.exception.TagNotFoundException;
import com.sideproject.codemoim.repository.*;
import org.assertj.core.util.introspection.PropertyOrFieldSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @InjectMocks
    ProfileService profileService;

    @Mock
    ProfileRepository profileRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    EmailRepository emailRepository;

    @Mock
    OauthInfoRepository oauthInfoRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    TagRepository tagRepository;

    @Mock
    PostRepository postRepository;

    @Mock
    ProfileFollowRepository profileFollowRepository;

    @Test
    @DisplayName("프로필 정보 조회 성공 테스트")
    void searchProfileByUserIdTest_success() {
        ProfileDto profileDto = new ProfileDto();

        Email email = Email.builder()
                .email("tester@naver.com")
                .build();

        OauthInfo oauthInfo = OauthInfo.builder()
                .build();

        List<Role> roles = new ArrayList<>();

        given(profileRepository.searchProfileDtoByUserId(anyLong())).willReturn(profileDto);
        given(emailRepository.searchEmailByUserId(anyLong())).willReturn(email);
        //given(oauthInfoRepository.findByUserId(anyLong())).willReturn(Optional.ofNullable(oauthInfo));
        given(roleRepository.searchRoleByUserId(anyLong())).willReturn(roles);

        ProfileDto profileDtoInfo = profileService.searchProfileByUserId(anyLong());

        Assertions.assertNotNull(profileDtoInfo.getEmail());
    }

    @Test
    @DisplayName("프로필 정보 조회 실패 테스트")
    void searchProfileByUserIdTest_fail() {
        ProfileDto profileDto = new ProfileDto();

        OauthInfo oauthInfo = OauthInfo.builder()
                .build();

        List<Role> roles = new ArrayList<>();

        given(profileRepository.searchProfileDtoByUserId(anyLong())).willReturn(profileDto);
        given(emailRepository.searchEmailByUserId(anyLong())).willReturn(null);
        given(oauthInfoRepository.findByUserId(anyLong())).willReturn(Optional.ofNullable(oauthInfo));
        given(roleRepository.searchRoleByUserId(anyLong())).willReturn(roles);

        ProfileDto profileDtoInfo = profileService.searchProfileByUserId(anyLong());

        Assertions.assertEquals(profileDtoInfo.getEmail(), "");
    }

    @Test
    @DisplayName("프로필 수정 성공 테스트")
    void modifyProfileTest_success() {
        Profile profile = Profile.builder().build();
        Email email = Email.builder().build();
        User user = User.builder().build();

        given(profileRepository.searchProfileByUserId(anyLong())).willReturn(profile);
        given(emailRepository.searchEmailByUserId(anyLong())).willReturn(email);
        given(userRepository.searchUserByIdAndStatus(anyLong())).willReturn(Optional.ofNullable(user));

        Map<String, Object> profileInfo = new HashMap<>();
        profileInfo.put("username", "tester");
        profileInfo.put("email", "tester@naver.com");
        profileInfo.put("profileUrl", "https://profile_img_url.com/profile/img.png");

        Assertions.assertDoesNotThrow(() -> {
            profileService.modifyProfile(profileInfo, anyLong());
        });
    }

    @Test
    @DisplayName("프로필 수정 실패 테스트")
    void modifyProfileTest_fail() {
        given(profileRepository.searchProfileByUserId(anyLong())).willReturn(null);
        given(emailRepository.searchEmailByUserId(anyLong())).willReturn(null);
        given(userRepository.searchUserByIdAndStatus(anyLong())).willReturn(null);

        Map<String, Object> profileInfo = new HashMap<>();
        profileInfo.put("username", "tester");
        profileInfo.put("email", "tester@naver.com");
        profileInfo.put("profileUrl", "https://profile_img_url.com/profile/img.png");

        Assertions.assertThrows(NullPointerException.class, () -> {
            profileService.modifyProfile(profileInfo, anyLong());
        });
    }

    @Test
    @DisplayName("프로필 이름 중복체크 성공 테스트")
    void usernameDuplicateCheckTest_success() {
        Profile profile = Profile.builder().build();

        given(profileRepository.duplicateCheckUsername(anyString())).willReturn(profile);

        Assertions.assertDoesNotThrow(() -> {
            profileService.duplicateCheckUsername(anyString());
        });
    }

    @Test
    @DisplayName("프로필 이름 중복체크 실패 테스트")
    void usernameDuplicateCheckTest_fail() throws UnsupportedEncodingException {
        given(profileRepository.duplicateCheckUsername(anyString())).willReturn(null);

        boolean check = profileService.duplicateCheckUsername(anyString());

        Assertions.assertFalse(check);
    }

    @Test
    @DisplayName("프로필 순위 조회 성공 테스트")
    void searchProfileRankTest_success() {
        List<ProfileDto> profileDtoList = new ArrayList<>();

        given(profileRepository.searchProfileRank()).willReturn(profileDtoList);

        Assertions.assertDoesNotThrow(() -> {
            profileService.searchProfileRank();
        });
    }

    @Test
    @DisplayName("프로필 태그 팔로우 성공 테스트")
    void followTagTest_success() {
        Map<String, Object> followInfo = new HashMap<>();
        followInfo.put("id", 1);
        followInfo.put("tagName", "test");

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .tags(new LinkedHashSet<>())
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        Tag tag = Tag.builder()
                .profileFollowerCount(0)
                .build();

        given(tagRepository.findByName(anyString())).willReturn(tag);

        Assertions.assertDoesNotThrow(() -> {
            profileService.followTag(followInfo, anyLong());
        });
    }

    @Test
    @DisplayName("프로필 태그 팔로우 실패 테스트")
    void followTagTest_fail() {
        Map<String, Object> followInfo = new HashMap<>();
        followInfo.put("id", 1);
        followInfo.put("tagName", "test");

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            profileService.followTag(followInfo, anyLong());
        });

        Assertions.assertThrows(TagNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .tags(new LinkedHashSet<>())
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            profileService.followTag(followInfo, anyLong());
        });
    }

    @Test
    @DisplayName("프로필 태그 언팔로우 성공 테스트")
    void unfollowTagTest_success() {
        Map<String, Object> followInfo = new HashMap<>();
        followInfo.put("id", 1);
        followInfo.put("tagName", "test");

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .tags(new LinkedHashSet<>())
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        Tag tag = Tag.builder()
                .profileFollowerCount(0)
                .build();

        given(tagRepository.findByName(anyString())).willReturn(tag);

        Assertions.assertDoesNotThrow(() -> {
            profileService.unfollowTag(followInfo, anyLong());
        });
    }

    @Test
    @DisplayName("프로필 태그 언팔로우 실패 테스트")
    void unfollowTagTest_fail() {
        Map<String, Object> followInfo = new HashMap<>();
        followInfo.put("id", 1);
        followInfo.put("tagName", "test");

        Assertions.assertThrows(ProfileNotFoundException.class, () -> {
            profileService.unfollowTag(followInfo, anyLong());
        });

        Assertions.assertThrows(TagNotFoundException.class, () -> {
            User user = mock(User.class);

            Profile profile = Profile.builder()
                    .user(user)
                    .tags(new LinkedHashSet<>())
                    .build();

            given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

            profileService.unfollowTag(followInfo, anyLong());
        });
    }

    @Test
    @DisplayName("프로필 전체 정보 조회 성공 테스트")
    void searchProfileTotalInfoTest_success() {
        given(profileRepository.validateProfile(anyLong())).willReturn(true);
        given(profileRepository.searchProfileInfo(anyLong())).willReturn(new ProfileDetailInfoDto());

        Pageable pageable = PageRequest.of(0, 5);

        given(profileRepository.searchFollowingInfo(any(), anyLong())).willReturn(new PageImpl<>(new ArrayList<>(), pageable, 0));

        given(postRepository.searchPostListByProfileId(any(), anyLong())).willReturn(new PageImpl<>(new ArrayList<>(), pageable, 0));

        Assertions.assertDoesNotThrow(() -> {
            profileService.searchProfileTotalInfo(1L);
        });
    }

    @Test
    @DisplayName("팔로잉 정보 조회 성공 테스트")
    void searchFollowingInfoTest_success() {
        Pageable pageable = PageRequest.of(0, 5);

        given(profileRepository.searchFollowingInfo(any(), anyLong())).willReturn(new PageImpl<>(new ArrayList<>(), pageable, 0));

        Assertions.assertDoesNotThrow(() -> {
            profileService.searchFollowingInfo(pageable, 1L);
        });
    }

    @Test
    @DisplayName("팔로워 정보 조회 성공 테스트")
    void searchFollowerInfoTest_success() {
        Pageable pageable = PageRequest.of(0, 5);

        given(profileRepository.searchFollowerInfo(any(), anyLong())).willReturn(new PageImpl<>(new ArrayList<>(), pageable, 0));

        Assertions.assertDoesNotThrow(() -> {
            profileService.searchFollowerInfo(pageable, 1L);
        });
    }

    @Test
    @DisplayName("태그 정보 조회 성공 테스트")
    void searchTagInfoTest_success() {
        Profile profile = mock(Profile.class);
        Post post = Post.builder().build();
        Post post1 = Post.builder().build();
        Post post2 = Post.builder().build();
        Post post3 = Post.builder().build();
        Post post4 = Post.builder().build();
        Post post5 = Post.builder().build();

        profile.addPost(post);
        profile.addPost(post1);
        profile.addPost(post2);
        profile.addPost(post3);
        profile.addPost(post4);
        profile.addPost(post5);

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        Pageable pageable = PageRequest.of(0, 5);

        Assertions.assertDoesNotThrow(() -> {
            profileService.searchTagInfo(pageable, 1L);
        });
    }

    @Test
    @DisplayName("프로필 팔로우 성공 테스트")
    void followProfileTest_success() {
        Map<String, Object> followInfo = new HashMap<>();

        followInfo.put("profileId", 1);
        followInfo.put("followingId", 1);

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        ProfileFollow profileFollow = mock(ProfileFollow.class);

        when(profileFollow.getId()).thenReturn(1L);

        given(profileFollowRepository.save(any())).willReturn(profileFollow);

        Assertions.assertDoesNotThrow(() -> {
            profileService.followProfile(followInfo, anyLong());
        });
    }

    @Test
    @DisplayName("프로필 언팔로우 성공 테스트")
    void unfollowProfileTest_success() {
        Map<String, Object> followInfo = new HashMap<>();

        followInfo.put("profileId", 1);
        followInfo.put("followingId", 1);

        User user = mock(User.class);

        Profile profile = Profile.builder()
                .user(user)
                .build();

        given(profileRepository.searchProfileById(anyLong())).willReturn(profile);

        ProfileFollow profileFollow = ProfileFollow.builder().build();

        given(profileFollowRepository.searchFollowingProfile(anyLong(), anyLong())).willReturn(profileFollow);

        Assertions.assertDoesNotThrow(() -> {
            profileService.unfollowProfile(followInfo, anyLong());
        });
    }

}