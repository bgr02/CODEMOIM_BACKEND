package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Email;
import com.sideproject.codemoim.domain.OauthInfo;
import com.sideproject.codemoim.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OauthInfoRepository extends JpaRepository<OauthInfo, Long> {
    Optional<OauthInfo> findByPlatformUserIdAndProvider(String platformUserId, String provider);
    Optional<OauthInfo> findByUserId(Long userId);
}
