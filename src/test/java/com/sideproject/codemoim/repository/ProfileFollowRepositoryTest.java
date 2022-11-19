package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.config.TestConfig;
import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.domain.ProfileFollow;
import com.sideproject.codemoim.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@DataJpaTest
@Import({TestConfig.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProfileFollowRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    ProfileFollowRepository profileFollowRepository;

    @Test
    @DisplayName("프로필 아이디를 사용한 팔로워 리스트 조회 테스트")
    void searchFollowerByProfileIdTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Profile follower = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .build();

        Profile saveFollowing = profileRepository.save(follower);

        ProfileFollow profileFollow = ProfileFollow.builder()
                .profile(saveProfile)
                .following(saveFollowing)
                .build();

        ProfileFollow saveProfileFollow = profileFollowRepository.save(profileFollow);

        List<Profile> followerList = profileFollowRepository.searchFollowerByProfileId(saveFollowing.getId());

        Assertions.assertEquals(saveProfileFollow.getProfile().getId(), followerList.get(0).getId());
    }

    @Test
    @DisplayName("사용자의 모든 팔로잉 제거")
    void deleteFollowing() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Profile follower = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .build();

        Profile saveFollowing = profileRepository.save(follower);

        ProfileFollow profileFollow = ProfileFollow.builder()
                .profile(saveProfile)
                .following(saveFollowing)
                .build();

        profileFollowRepository.save(profileFollow);

        int deleteFollowingCount = profileFollowRepository.deleteFollowing(saveProfile);

        Assertions.assertEquals(deleteFollowingCount, 1);
    }

    @Test
    @DisplayName("사용자를 팔로우 한 모든 케이스를 제거")
    void deleteFollower() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Profile follower = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .build();

        Profile saveFollowing = profileRepository.save(follower);

        ProfileFollow profileFollow = ProfileFollow.builder()
                .profile(saveProfile)
                .following(saveFollowing)
                .build();

        profileFollowRepository.save(profileFollow);

        int deleteFollowerCount = profileFollowRepository.deleteFollower(saveFollowing);

        Assertions.assertEquals(deleteFollowerCount, 1);
    }

    @Test
    @DisplayName("프로필-팔로우 정보 조회 테스트")
    void searchFollowingProfileTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        User user2 = User.builder()
                .username("tester2")
                .status((byte) 0)
                .build();

        User saveUser2 = userRepository.save(user2);

        Profile profile = Profile.builder()
                .user(saveUser)
                .username("tester")
                .contributionPoint(0)
                .build();

        Profile saveProfile = profileRepository.save(profile);

        Profile follower = Profile.builder()
                .user(saveUser2)
                .username("tester2")
                .contributionPoint(0)
                .build();

        Profile saveFollowing = profileRepository.save(follower);

        ProfileFollow profileFollow = ProfileFollow.builder()
                .profile(saveProfile)
                .following(saveFollowing)
                .build();

        ProfileFollow saveProfileFollow = profileFollowRepository.save(profileFollow);

        ProfileFollow searchProfileFollow = profileFollowRepository.searchFollowingProfile(saveProfile.getId(), saveFollowing.getId());

        Assertions.assertEquals(searchProfileFollow.getId(), saveProfileFollow.getId());
    }

}
