package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.config.TestConfig;
import com.sideproject.codemoim.domain.Role;
import com.sideproject.codemoim.domain.RoleName;
import com.sideproject.codemoim.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@DataJpaTest
@Import({TestConfig.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class RoleRepositoryTest {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("역할 이름을 사용한 역할 검색 테스트")
    void findByName() {
        Role role = Role.builder()
                .name(RoleName.ROLE_USER)
                .build();

        Role saveRole = roleRepository.save(role);

        Role findRole = roleRepository.findByName(saveRole.getName());

        Assertions.assertNotNull(findRole);
    }

    @Test
    @DisplayName("사용자 아이디를 사용한 역할 검색 테스트")
    void searchRoleByUserId() {
        Role role = Role.builder()
                .name(RoleName.ROLE_USER)
                .build();

        Role saveRole = roleRepository.save(role);

        List<Role> roles = new ArrayList<>();

        roles.add(role);

        User user = User.builder()
                .username("tester")
                .roles(roles)
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        List<Role> findRoles = roleRepository.searchRoleByUserId(saveUser.getId());

        Assertions.assertFalse(findRoles.isEmpty());
    }

}