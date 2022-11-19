package com.sideproject.codemoim.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.sideproject.codemoim.domain.QComment.comment;
import static com.sideproject.codemoim.domain.QPost.post;
import static com.sideproject.codemoim.domain.QProfile.profile;
import static com.sideproject.codemoim.domain.QProfileFollow.profileFollow;
import static com.sideproject.codemoim.domain.QRole.role;
import static com.sideproject.codemoim.domain.QTag.tag;
import static com.sideproject.codemoim.domain.QUser.user;

@RequiredArgsConstructor
public class CustomProfileRepositoryImpl implements CustomProfileRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public ProfileDto searchProfileDtoByUserId(Long userId) {
        return queryFactory
                .select(Projections.bean(ProfileDto.class,
                        profile.id,
                        //Expressions.asNumber(userId).as("userId"),
                        profile.username,
                        profile.profileImgUrl,
                        profile.contributionPoint,
                        user.status.as("userStatus")
                ))
                .from(profile)
                .join(profile.user, user)
                .where(profile.user.id.eq(userId), user.status.ne((byte) 2))
                .fetchOne();
    }

    @Override
    public Profile searchProfileByUserId(Long userId) {
        return queryFactory
                .selectFrom(profile)
                .join(profile.user, user).fetchJoin()
                .where(profile.user.id.eq(userId), user.status.ne((byte) 2))
                .fetchOne();
    }

    @Override
    public Profile duplicateCheckUsername(String username) {
        return queryFactory
                .selectFrom(profile)
                .join(profile.user, user)
                .where(profile.username.eq(username), user.status.ne((byte) 2))
                .fetchOne();
    }

    @Override
    public boolean followTagExist(Long id) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(profile)
                .join(profile.tags, tag)
                .where(tag.id.eq(id))
                .fetchOne();

        return fetchOne != null;
    }

    @Override
    public Profile searchScrapByProfileAndPost(long profileId, long postId) {
        return queryFactory
                .select(profile)
                .from(profile)
                .join(profile.scraps, post)
                .where(profile.id.eq(profileId), post.id.eq(postId), post.status.eq(true))
                .fetchOne();
    }

    @Override
    public List<Tag> searchProfileFollowTags(Long id) {
        return queryFactory
                .select(tag)
                .from(profile)
                .join(profile.tags, tag)
                .where(profile.id.eq(id))
                .orderBy(tag.id.asc())
                .fetch();
    }

    @Override
    public List<ProfileDto> searchProfileRank() {
        return queryFactory
                .select(
                        Projections.bean(ProfileDto.class,
                                profile.id,
                                profile.username,
                                profile.profileImgUrl,
                                profile.contributionPoint,
                                user.status.as("userStatus")
                        )
                )
                .from(profile)
                .join(profile.user, user)
                .join(user.roles, role)
                .where(profile.contributionPoint.ne(0), user.status.ne((byte) 2), role.name.eq(RoleName.ROLE_USER))
                .offset(0)
                .limit(10)
                .orderBy(profile.contributionPoint.desc())
                .fetch();
    }

    @Override
    public boolean validateProfile(Long id) {
        Profile profile = queryFactory
                .selectFrom(QProfile.profile)
                .join(QProfile.profile.user, user)
                .where(QProfile.profile.id.eq(id), user.status.ne((byte) 2))
                .fetchOne();

        return profile != null;
    }

    @Override
    public ProfileDetailInfoDto searchProfileInfo(Long id) {
        ProfileDetailInfoDto profileDetailInfoDto = queryFactory
                .select(Projections.bean(ProfileDetailInfoDto.class, profile.id, profile.username, profile.profileImgUrl, profile.contributionPoint))
                .from(profile)
                .join(profile.user, user)
                .where(profile.id.eq(id), user.status.ne((byte) 2))
                .fetchOne();

        QProfile qProfile = new QProfile("qProfile");
        QProfile following = new QProfile("following");

        long followingCount = queryFactory
                .selectFrom(profileFollow)
                .join(profileFollow.profile, qProfile)
                .join(profileFollow.following, following)
                .where(profileFollow.profile.id.eq(id), profileFollow.following.user.status.ne((byte) 2))
                .fetchCount();

        profileDetailInfoDto.setFollowingCount((int) followingCount);

        long followerCount = queryFactory
                .selectFrom(profileFollow)
                .join(profileFollow.profile, qProfile)
                .join(profileFollow.following, following)
                .where(profileFollow.profile.user.status.ne((byte) 2), profileFollow.following.id.eq(id))
                .fetchCount();

        profileDetailInfoDto.setFollowerCount((int) followerCount);

        long tagCount = queryFactory
                .select(tag)
                .from(profile)
                .join(profile.tags, tag)
                .where(profile.id.eq(id))
                .fetchCount();

        profileDetailInfoDto.setTagCount((int) tagCount);

        long postCount = queryFactory
                .selectFrom(post)
                .join(post.profile, profile)
                .where(profile.id.eq(id), post.status.eq(true))
                .fetchCount();

        profileDetailInfoDto.setPostCount((int) postCount);

        long commentCount = queryFactory
                .selectFrom(comment)
                .join(comment.profile, profile)
                .where(profile.id.eq(id))
                .fetchCount();

        profileDetailInfoDto.setCommentCount((int) commentCount);

        long scrapCount = queryFactory
                .select(post)
                .from(profile)
                .join(profile.scraps, post)
                .where(profile.id.eq(id), post.status.eq(true))
                .fetchCount();

        profileDetailInfoDto.setScrapCount((int) scrapCount);

        return profileDetailInfoDto;
    }

    @Override
    public Profile searchProfileById(Long id) {
        return queryFactory
                .selectFrom(profile)
                .join(profile.user, user)
                .where(profile.id.eq(id), user.status.ne((byte) 2))
                .fetchOne();
    }

    @Override
    public boolean searchFollowTagExist(Long profileId, Long tagId) {
        Tag tag = queryFactory
                .select(QTag.tag)
                .from(profile)
                .join(profile.tags, QTag.tag)
                .where(profile.id.eq(profileId), QTag.tag.id.eq(tagId))
                .fetchOne();

        return tag != null;
    }

    @Override
    public Page<FollowRelationDto> searchFollowingInfo(Pageable pageable, Long id) {
        List<FollowRelationDto> content = queryFactory
                .select(Projections.bean(FollowRelationDto.class, profileFollow.following.id.as("id"),
                        profileFollow.following.username.as("name"), profileFollow.following.profileImgUrl.as("imgUrl")))
                .from(profileFollow)
                .join(profileFollow.profile, profile)
                .join(profileFollow.following.user, user)
                .where(profile.id.eq(id), user.status.ne((byte) 2))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long count = queryFactory
                .select(profileFollow.following)
                .from(profileFollow)
                .join(profileFollow.profile, profile)
                .join(profileFollow.following.user, user)
                .where(profile.id.eq(id), user.status.ne((byte) 2))
                .fetchCount();

        return new PageImpl<>(content, pageable, count);
    }

    @Override
    public Page<FollowRelationDto> searchFollowerInfo(Pageable pageable, Long id) {
        List<FollowRelationDto> content = queryFactory
                .select(Projections.bean(FollowRelationDto.class, profileFollow.profile.id.as("id"),
                        profileFollow.profile.username.as("name"), profileFollow.profile.profileImgUrl.as("imgUrl")))
                .from(profileFollow)
                .join(profileFollow.following, profile)
                .join(profileFollow.profile.user, user)
                .where(profile.id.eq(id), user.status.ne((byte) 2))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long count = queryFactory
                .select(profileFollow.profile)
                .from(profileFollow)
                .join(profileFollow.following, profile)
                .join(profileFollow.profile.user, user)
                .where(profile.id.eq(id), user.status.ne((byte) 2))
                .fetchCount();

        return new PageImpl<>(content, pageable, count);
    }

}
