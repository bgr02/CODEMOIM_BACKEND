package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.config.TestConfig;
import com.sideproject.codemoim.domain.Email;
import com.sideproject.codemoim.domain.Role;
import com.sideproject.codemoim.domain.RoleName;
import com.sideproject.codemoim.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@DataJpaTest
@Import({TestConfig.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmailRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    EmailRepository emailRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("유저 엔티티를 사용한 이메일 검색 테스트")
    void findByUserIdTest() {
        Role role = Role.builder()
                .name(RoleName.ROLE_USER)
                .build();

        Role saveRole = roleRepository.save(role);

        List<Role> roles = new ArrayList<>();
        roles.add(saveRole);

        User user = User.builder()
                .username("tester")
                .password(passwordEncoder.encode("1234"))
                .status((byte) 0)
                .roles(roles)
                .build();

        User saveUser = userRepository.save(user);

        Email email = Email.builder()
                .user(saveUser)
                .email("tester@naver.com")
                .build();

        Email saveEmail = emailRepository.save(email);

        Optional<Email> optionalEmail = emailRepository.findByUser(saveUser);

        optionalEmail.ifPresent(email1 -> {
            Assertions.assertEquals(saveEmail.getEmail() , email.getEmail());
        });
    }

    @Test
    @DisplayName("이메일을 사용한 이메일 엔티티 검색 테스트")
    void searchEmailByEmailTest() {
        Role role = Role.builder()
                .name(RoleName.ROLE_USER)
                .build();

        Role saveRole = roleRepository.save(role);

        List<Role> roles = new ArrayList<>();
        roles.add(saveRole);

        User user = User.builder()
                .username("tester")
                .password(passwordEncoder.encode("1234"))
                .status((byte) 0)
                .roles(roles)
                .build();

        User saveUser = userRepository.save(user);

        Email email = Email.builder()
                .user(saveUser)
                .email("tester@naver.com")
                .build();

        Email saveEmail = emailRepository.save(email);

        Optional<Email> optionalEmail = Optional.ofNullable(emailRepository.searchEmailByEmail(saveEmail.getEmail()));

        optionalEmail.ifPresent(email1 -> {
            Assertions.assertEquals(saveEmail.getEmail(), email1.getEmail());
        });
    }

    @Test
    @DisplayName("유저 아이디를 사용한 이메일 검색 테스트")
    void searchEmailByUserIdTest() {
        Role role = Role.builder()
                .name(RoleName.ROLE_USER)
                .build();

        Role saveRole = roleRepository.save(role);

        List<Role> roles = new ArrayList<>();
        roles.add(saveRole);

        User user = User.builder()
                .username("tester")
                .password(passwordEncoder.encode("1234"))
                .status((byte) 0)
                .roles(roles)
                .build();

        User saveUser = userRepository.save(user);

        Email email = Email.builder()
                .user(saveUser)
                .email("tester@naver.com")
                .build();

        Email saveEmail = emailRepository.save(email);

        Email findEmail = emailRepository.searchEmailByUserId(saveUser.getId());

        Assertions.assertEquals(saveEmail.getEmail() , findEmail.getEmail());
    }

    @Test
    @DisplayName("이메일 비밀키를 사용한 이메일 검색 테스트")
    void searchEmailBySecretKeyTest() {
        Role role = Role.builder()
                .name(RoleName.ROLE_USER)
                .build();

        Role saveRole = roleRepository.save(role);

        List<Role> roles = new ArrayList<>();
        roles.add(saveRole);

        User user = User.builder()
                .username("tester")
                .password(passwordEncoder.encode("1234"))
                .status((byte) 0)
                .roles(roles)
                .build();

        User saveUser = userRepository.save(user);

        Email email = Email.builder()
                .user(saveUser)
                .email("tester@naver.com")
                .secretKey(UUID.randomUUID().toString())
                .expiredDate(LocalDateTime.now().plusMinutes(10))
                .build();

        Email saveEmail = emailRepository.save(email);

        Email findEmail = emailRepository.searchEmailBySecretKey(saveEmail.getSecretKey());

        Assertions.assertEquals(saveEmail, findEmail);
    }

    @Test
    @DisplayName("이메일 비밀키 유효성 검증 테스트")
    void verifySecretKeyTest() {
        Role role = Role.builder()
                .name(RoleName.ROLE_USER)
                .build();

        Role saveRole = roleRepository.save(role);

        List<Role> roles = new ArrayList<>();
        roles.add(saveRole);

        User user = User.builder()
                .username("tester")
                .password(passwordEncoder.encode("1234"))
                .status((byte) 0)
                .roles(roles)
                .build();

        User saveUser = userRepository.save(user);

        Email email = Email.builder()
                .user(saveUser)
                .email("tester@naver.com")
                .secretKey(UUID.randomUUID().toString())
                .expiredDate(LocalDateTime.now().plusMinutes(10))
                .build();

        Email saveEmail = emailRepository.save(email);

        Email findEmail = emailRepository.verifySecretKey(saveEmail.getSecretKey());

        Assertions.assertNotNull(findEmail);
    }

}
