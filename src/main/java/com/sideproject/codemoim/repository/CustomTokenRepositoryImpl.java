package com.sideproject.codemoim.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.codemoim.domain.QToken;
import com.sideproject.codemoim.domain.QUser;
import com.sideproject.codemoim.domain.Token;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static com.sideproject.codemoim.domain.QToken.token;
import static com.sideproject.codemoim.domain.QUser.user;

@RequiredArgsConstructor
public class CustomTokenRepositoryImpl implements CustomTokenRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean searchRefreshToken(Long userId, String refreshToken, LocalDateTime expiredDate, String provider) {
        Token token = queryFactory.
                selectFrom(QToken.token)
                .where(user.id.eq(userId), QToken.token.refreshToken.eq(refreshToken), QToken.token.expiredDate.after(expiredDate), QToken.token.provider.eq(provider))
                .fetchOne();

        return token != null;
    }

}
