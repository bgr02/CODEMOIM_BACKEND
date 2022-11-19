package com.sideproject.codemoim.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.codemoim.domain.Email;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static com.sideproject.codemoim.domain.QEmail.email1;
import static com.sideproject.codemoim.domain.QUser.user;

@RequiredArgsConstructor
public class CustomEmailRepositoryImpl implements CustomEmailRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Email searchEmailByUserId(Long userId) {
        return queryFactory
                .selectFrom(email1)
                .join(email1.user, user)
                .where(email1.user.id.eq(userId), user.status.ne((byte) 2))
                .fetchOne();
    }

    @Override
    public Email searchEmailBySecretKey(String secretKey) {
        return queryFactory
                .selectFrom(email1)
                .join(email1.user, user)
                .where(email1.secretKey.eq(secretKey), user.status.ne((byte) 2))
                .fetchOne();
    }

    @Override
    public Email verifySecretKey(String secretKey) {
        return queryFactory
                .selectFrom(email1)
                .join(email1.user, user).fetchJoin()
                .where(email1.secretKey.eq(secretKey), email1.expiredDate.after(LocalDateTime.now()), user.status.ne((byte) 2))
                .fetchOne();
    }

    @Override
    public Email searchEmailByEmail(String email) {
        return queryFactory
                .selectFrom(email1)
                .join(email1.user, user).fetchJoin()
                .where(email1.email.eq(email), user.status.ne((byte) 2))
                .fetchOne();
    }

}
