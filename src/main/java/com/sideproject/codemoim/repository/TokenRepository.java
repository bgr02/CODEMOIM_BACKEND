package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Token;
import com.sideproject.codemoim.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
public interface TokenRepository extends JpaRepository<Token, Long>, CustomTokenRepository {
    Optional<Token> findByUser(User user);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from Token t where t.user = :user and t.expiredDate < :currentDate")
    int deleteTokenByUserAndExpiredDate(@Param("user") User user, @Param("currentDate") LocalDateTime currentDate);
}
