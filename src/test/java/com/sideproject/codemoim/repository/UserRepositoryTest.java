package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.config.TestConfig;
import com.sideproject.codemoim.domain.User;
import com.sun.source.tree.AssertTree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

@DataJpaTest
@Import({TestConfig.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("유저 이름을 사용한 유저 조회 테스트")
    void findByUsernameTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Optional<User> optionalUser = userRepository.findByUsername(saveUser.getUsername());

        optionalUser.ifPresent(user1 -> {
            Assertions.assertEquals(saveUser.getUsername(), optionalUser.get().getUsername());
        });
    }

    @Test
    @DisplayName("유저 비밀번호 변경 비밀키 유효성 검증 테스트")
    void verifySecretKeyTest() {
        User user = User.builder()
                .username("tester")
                .password("1234")
                .passwordChangeKey("change_key")
                .passwordChangeKeyExpiredDate(LocalDateTime.now().plusMinutes(10))
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        User user1 = userRepository.verifySecretKey(saveUser.getPasswordChangeKey());

        Assertions.assertNotNull(user1);
    }

    @Test
    @DisplayName("유저 아이디와 상태값을 사용한 유저 검색 테스트")
    void searchUserByIdAndStatusTest() {
        User user = User.builder()
                .username("tester")
                .password("1234")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Optional<User> optionalUser = userRepository.searchUserByIdAndStatus(saveUser.getId());

        Assertions.assertNotNull(optionalUser.get());
    }

}