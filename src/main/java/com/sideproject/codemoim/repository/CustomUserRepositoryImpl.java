package com.sideproject.codemoim.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.codemoim.domain.QUser;
import com.sideproject.codemoim.domain.User;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.sideproject.codemoim.domain.QUser.user;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<User> searchUserByIdAndStatus(Long id) {
        User user = queryFactory
                .selectFrom(QUser.user)
                .where(QUser.user.id.eq(id), QUser.user.status.ne((byte) 2))
                .fetchOne();

        return Optional.ofNullable(user);
    }

    @Override
    public User usernameDuplicateCheck(String username) {
        return queryFactory
                .selectFrom(user)
                .where(user.username.eq(username), user.status.ne((byte) 2))
                .fetchOne();
    }

    @Override
    public User verifySecretKey(String key) {
        return queryFactory
                .selectFrom(user)
                .where(user.passwordChangeKey.eq(key), user.passwordChangeKeyExpiredDate.after(LocalDateTime.now()), user.status.ne((byte) 2))
                .fetchOne();
    }

}
