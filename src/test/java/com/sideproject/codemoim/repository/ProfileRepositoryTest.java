package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.config.TestConfig;
import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.dto.FollowRelationDto;
import com.sideproject.codemoim.dto.ProfileDto;
import com.sideproject.codemoim.dto.ProfileDetailInfoDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

@DataJpaTest
@Import({TestConfig.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;
    
    @Autowired
    TagRepository tagRepository;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    ProfileFollowRepository profileFollowRepository;

    @Autowired
    RoleRepository roleRepository;

    @Test
    @DisplayName("프로필 DTO 조회 테스트")
    void searchProfileDtoByUserIdTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        ProfileDto profileDto = profileRepository.searchProfileDtoByUserId(saveUser.getId());
        Optional<Profile> optionalProfile = profileRepository.findById(profileDto.getId());

        Assertions.assertEquals(profileDto.getId(), optionalProfile.get().getId());
    }

    @Test
    @DisplayName("프로필 정보 조회 테스트")
    void searchProfileByUserIdTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Profile profile1 = profileRepository.searchProfileByUserId(saveUser.getId());
        Optional<Profile> optionalProfile = profileRepository.findById(profile1.getId());

        Assertions.assertEquals(profile1.getId(), optionalProfile.get().getId());
    }

    @Test
    @DisplayName("프로필 이름 중복체크 테스트")
    void usernameDuplicateCheckTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Profile profile1 = profileRepository.duplicateCheckUsername("tester");
        Optional<Profile> optionalProfile = profileRepository.findById(profile1.getId());

        Assertions.assertEquals(profile1.getUsername(), optionalProfile.get().getUsername());
    }

    @Test
    @DisplayName("팔로우 태그 존재여부 체크 테스트")
    void followTagExistTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Tag tag = Tag
                .builder()
                .name("test")
                .profileFollowerCount(0)
                .postTagCount(0)
                .build();

        Tag saveTag = tagRepository.save(tag);

        Set<Tag> tagList = new LinkedHashSet<>();
        tagList.add(saveTag);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .tags(tagList)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        boolean tagExist = profileRepository.followTagExist(saveTag.getId());

        Assertions.assertTrue(tagExist);
    }

    @Test
    @DisplayName("프로필에서 스크랩한 포스트 조회 테스트")
    void searchScrapByProfileAndPostTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile2 = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .scraps(new LinkedHashSet<>())
                .contributionPoint(0)
                .build();

        Profile saveProfile2 = profileRepository.save(profile2);

        Board board = Board.builder()
                .name("test")
                .status(true)
                .url("/test")
                .icon("test")
                .sort(0)
                .display(false)
                .authority("USER")
                .build();

        Board saveBoard = boardRepository.save(board);

        Post post = Post.builder()
                .board(saveBoard)
                .profile(saveProfile)
                .title("test")
                .content("test")
                .status(true)
                .viewCount(0)
                .totalThumbsupVoteCount(0)
                .totalThumbsdownVoteCount(0)
                .build();

        Post savePost = postRepository.save(post);

        saveProfile2.addPost(savePost);

        Profile scrapUser = profileRepository.searchScrapByProfileAndPost(saveProfile2.getId(), savePost.getId());

        Assertions.assertEquals(saveProfile2.getId(), scrapUser.getId());
    }

    @Test
    @DisplayName("프로필에서 팔로우한 태그 리스트 조회 테스트")
    void searchProfileFollowTagsTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Tag tag = Tag
                .builder()
                .name("test")
                .profileFollowerCount(0)
                .postTagCount(0)
                .build();

        Tag saveTag = tagRepository.save(tag);

        Set<Tag> tagList = new LinkedHashSet<>();
        tagList.add(saveTag);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .tags(tagList)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        List<Tag> tags = profileRepository.searchProfileFollowTags(saveProfile.getId());

        Assertions.assertEquals(saveTag.getId(), tags.get(0).getId());
    }

    @Test
    @DisplayName("프로필 랭크 리스트 조회 테스트")
    void searchProfileRankTest() {
        Role role = Role.builder()
                .name(RoleName.ROLE_USER)
                .build();

        Role saveRole = roleRepository.save(role);

        List<Role> roles = new ArrayList<>();
        roles.add(saveRole);

        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .roles(roles)
                .build();

        User saveUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(100)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .roles(roles)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile2 = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(200)
                .build();

        Profile saveProfile2 = profileRepository.save(profile2);

        List<ProfileDto> profileDtoList = profileRepository.searchProfileRank();

        Assertions.assertEquals(saveProfile2.getId(), profileDtoList.get(0).getId());
    }

    @Test
    @DisplayName("프로필 유효성 검증 테스트")
    void validateProfileTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(100)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        boolean validateProfile = profileRepository.validateProfile(saveProfile.getId());

        Assertions.assertEquals(validateProfile, true);
    }

    @Test
    @DisplayName("프로필 정보 조회 테스트")
    void searchProfileInfoTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(100)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        ProfileDetailInfoDto profileDetailInfoDto = profileRepository.searchProfileInfo(saveProfile.getId());

        Assertions.assertEquals(saveProfile.getId(), profileDetailInfoDto.getId());
    }

    @Test
    @DisplayName("프로필 조회 테스트")
    void searchProfileByIdTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(100)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Profile searchProfile = profileRepository.searchProfileById(saveProfile.getId());

        Assertions.assertEquals(saveProfile.getId(), searchProfile.getId());
    }

    @Test
    @DisplayName("팔로잉 조회 테스트")
    void searchFollowingInfoTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile2 = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .build();

        Profile saveProfile2 = profileRepository.save(profile2);

        ProfileFollow profileFollow = ProfileFollow.builder()
                .profile(saveProfile)
                .following(saveProfile2)
                .build();

        ProfileFollow saveProfileFollow = profileFollowRepository.save(profileFollow);

        Pageable pageable = PageRequest.of(0, 5);

        Page<FollowRelationDto> followRelationDtos = profileRepository.searchFollowingInfo(pageable, saveProfile.getId());

        Assertions.assertEquals(followRelationDtos.getContent().get(0).getId(), saveProfile2.getId());
    }

    @Test
    @DisplayName("팔로워 조회 테스트")
    void searchFollowerInfoTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile2 = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .build();

        Profile saveProfile2 = profileRepository.save(profile2);

        ProfileFollow profileFollow = ProfileFollow.builder()
                .profile(saveProfile)
                .following(saveProfile2)
                .build();

        ProfileFollow saveProfileFollow = profileFollowRepository.save(profileFollow);

        Pageable pageable = PageRequest.of(0, 5);

        Page<FollowRelationDto> followRelationDtos = profileRepository.searchFollowerInfo(pageable, saveProfile2.getId());

        Assertions.assertEquals(followRelationDtos.getContent().get(0).getId(), saveProfile.getId());
    }

}