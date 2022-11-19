package com.sideproject.codemoim.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.codemoim.domain.Profile;
import com.sideproject.codemoim.domain.ProfileFollow;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.sideproject.codemoim.domain.QProfile.profile;
import static com.sideproject.codemoim.domain.QProfileFollow.profileFollow;
import static com.sideproject.codemoim.domain.QUser.user;

@RequiredArgsConstructor
public class CustomProfileFollowRepositoryImpl implements CustomProfileFollowRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Profile> searchFollowerByProfileId(Long id) {
        return queryFactory
                .select(profileFollow.profile)
                .from(profileFollow)
                .join(profileFollow.following, profile)
                .join(profileFollow.profile.user, user)
                .where(profile.id.eq(id), user.status.ne((byte) 2))
                .fetch();
    }

    @Override
    public List<Profile> searchFollowingByProfileId(Long id) {
        return queryFactory
                .select(profileFollow.following)
                .from(profileFollow)
                .join(profileFollow.profile, profile)
                .join(profileFollow.following.user, user)
                .where(profile.id.eq(id), user.status.ne((byte) 2))
                .fetch();
    }

    @Override
    public ProfileFollow searchFollowingProfile(Long profileId, Long followingId) {
        return queryFactory
                .selectFrom(profileFollow)
                .where(profileFollow.profile.id.eq(profileId), profileFollow.following.id.eq(followingId))
                .fetchOne();
    }

}
