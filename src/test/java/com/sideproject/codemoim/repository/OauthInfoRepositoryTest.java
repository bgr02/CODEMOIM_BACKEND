package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.config.TestConfig;
import com.sideproject.codemoim.domain.OauthInfo;
import com.sideproject.codemoim.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({TestConfig.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OauthInfoRepositoryTest {

    @Autowired
    OauthInfoRepository oauthInfoRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("플랫폼 정보를 사용한 Oauth 정보 검색 테스트")
    void findByPlatformUserIdAndProviderTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        OauthInfo oauthInfo = OauthInfo.builder()
                .user(saveUser)
                .platformUserId("1")
                .provider("test")
                .build();

        OauthInfo saveOauthInfo = oauthInfoRepository.save(oauthInfo);

        Optional<OauthInfo> optionalOauthInfo = oauthInfoRepository
                .findByPlatformUserIdAndProvider(saveOauthInfo.getPlatformUserId(), saveOauthInfo.getProvider());

        Assertions.assertNotNull(optionalOauthInfo.get());
    }

    @Test
    @DisplayName("유저 아이디를 사용한 Oauth 정보 검색 테스트")
    void findByUserIdTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        OauthInfo oauthInfo = OauthInfo.builder()
                .user(saveUser)
                .platformUserId("1")
                .provider("test")
                .build();

        OauthInfo saveOauthInfo = oauthInfoRepository.save(oauthInfo);

        Optional<OauthInfo> optionalOauthInfo = oauthInfoRepository.findByUserId(saveUser.getId());

        Assertions.assertNotNull(optionalOauthInfo.get());
    }

}