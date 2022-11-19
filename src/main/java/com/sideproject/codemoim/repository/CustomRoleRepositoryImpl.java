package com.sideproject.codemoim.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.codemoim.domain.Role;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.sideproject.codemoim.domain.QRole.role;
import static com.sideproject.codemoim.domain.QUser.user;

@RequiredArgsConstructor
public class CustomRoleRepositoryImpl implements CustomRoleRepository{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Role> searchRoleByUserId(Long userId) {
        return queryFactory
                .select(role)
                .from(user)
                .join(user.roles, role)
                .where(user.id.eq(userId), user.status.ne((byte) 2))
                .fetch();
    }

}
