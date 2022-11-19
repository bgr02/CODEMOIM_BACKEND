package com.sideproject.codemoim.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sideproject.codemoim.config.TestConfig;
import com.sideproject.codemoim.domain.Token;
import com.sideproject.codemoim.domain.User;
import com.sideproject.codemoim.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.servlet.http.Cookie;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@DataJpaTest
@Import({TestConfig.class})
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TokenRepositoryTest {

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    @DisplayName("유저를 사용한 토큰 검색 테스트")
    void findByUserTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        Token token = Token.builder()
                .user(saveUser)
                .refreshToken("refresh token")
                .expiredDate(LocalDateTime.now().plusMinutes(10))
                .provider("local")
                .build();

        tokenRepository.save(token);

        Optional<Token> optionalToken = tokenRepository.findByUser(saveUser);

        Assertions.assertNotNull(optionalToken.get());
    }

    @Test
    @DisplayName("사용자가 가진 토큰 검색 테스트")
    void searchRefreshTokenTest() {
        User user = User.builder()
                .username("tester")
                .status((byte) 0)
                .build();

        User saveUser = userRepository.save(user);

        LocalDateTime currentDate = LocalDateTime.now();

        Token token = Token.builder()
                .user(saveUser)
                .refreshToken("refresh token")
                .expiredDate(currentDate.plusMinutes(10))
                .provider("local")
                .build();

        tokenRepository.save(token);

        boolean tokenFlag = tokenRepository.searchRefreshToken(saveUser.getId(), token.getRefreshToken(), currentDate, token.getProvider());

        Assertions.assertTrue(tokenFlag);
    }

}