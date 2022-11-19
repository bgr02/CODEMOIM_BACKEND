package com.sideproject.codemoim.service;

import com.sideproject.codemoim.domain.Token;
import com.sideproject.codemoim.domain.User;
import com.sideproject.codemoim.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    @Transactional
    public void createToken(Token token) {
        tokenRepository.save(token);
    }

    @Transactional
    public boolean searchRefreshToken(Long userId, String refreshToken, LocalDateTime expiredDate, String provider) {
        return tokenRepository.searchRefreshToken(userId, refreshToken, expiredDate, provider);
    }

    @Transactional
    public void deleteTokenByUserAndExpiredDate(User user) {
        tokenRepository.deleteTokenByUserAndExpiredDate(user, LocalDateTime.now());
    }

}
