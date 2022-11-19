package com.sideproject.codemoim.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sideproject.codemoim.domain.Role;
import com.sideproject.codemoim.domain.RoleName;
import com.sideproject.codemoim.domain.User;
import com.sideproject.codemoim.property.CustomProperties;
import com.sideproject.codemoim.repository.RoleRepository;
import com.sideproject.codemoim.repository.UserRepository;
import com.sideproject.codemoim.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest()
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @BeforeEach
    void createUser() {
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

        Assertions.assertEquals(saveUser.getRoles().get(0), saveRole);
    }

    @Test
    @DisplayName("Csrf Token 발급 테스트")
    void csrfTokenTest() throws Exception {
        mockMvc
                .perform(post("/auth/csrf-token"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("로컬 로그인 테스트")
    void localLoginTest() throws Exception {
        Map<String, Object> param = new HashMap<>();

        param.put("username", "tester");
        param.put("password", "1234");

        MvcResult mvcResult = mockMvc
                .perform(post("/auth/login")
                        .contentType("application/json")
                        .characterEncoding("utf-8")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(param))
                )
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        Cookie accessToken = mvcResult.getResponse().getCookie("access_token");

        Jws<Claims> claimsJws = jwtUtil.parserToken(accessToken.getValue());
        long usrId = Long.parseLong(claimsJws.getBody().getSubject());

        Optional<User> optionalUser = userRepository.searchUserByIdAndStatus(usrId);

        optionalUser.ifPresent(user -> Assertions.assertEquals(user.getUsername(), "tester"));
    }

    @Test
    @DisplayName("토큰 재발급 테스트")
    void refreshTokenTest() throws Exception {
        Map<String, Object> param = new HashMap<>();

        param.put("username", "tester");
        param.put("password", "1234");

        MvcResult mvcResult = mockMvc
                .perform(post("/auth/login")
                        .contentType("application/json")
                        .characterEncoding("utf-8")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(objectMapper.writeValueAsString(param))
                )
                .andReturn();

        Cookie[] beforeCookies = mvcResult.getResponse().getCookies();

        mockMvc
                .perform(post("/auth/access-token")
                        .cookie(beforeCookies)
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
