package com.sideproject.codemoim.repository;

import java.time.LocalDateTime;

public interface CustomTokenRepository {
    boolean searchRefreshToken(Long userId, String refreshToken, LocalDateTime expiredDate, String provider);
}