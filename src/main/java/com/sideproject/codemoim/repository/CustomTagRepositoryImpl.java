package com.sideproject.codemoim.repository;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.codemoim.dto.TagCountDto;
import com.sideproject.codemoim.dto.TagDetailDto;
import com.sideproject.codemoim.dto.TagDto;
import com.sideproject.codemoim.util.OrderByNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.sideproject.codemoim.domain.QPost.post;
import static com.sideproject.codemoim.domain.QPostTag.postTag;
import static com.sideproject.codemoim.domain.QProfile.profile;
import static com.sideproject.codemoim.domain.QTag.tag;
import static com.sideproject.codemoim.domain.QUser.user;

@RequiredArgsConstructor
public class CustomTagRepositoryImpl implements CustomTagRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public boolean duplicateCheckName(String name) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(tag)
                .where(tag.name.eq(name))
                .fetchOne();

        return fetchOne != null;
    }

    @Override
    public Page<TagDto> searchTagList(Pageable pageable) {
        List<Long> ids = queryFactory
                .select(tag.id)
                .from(tag)
                .orderBy(tag.id.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (CollectionUtils.isEmpty(ids)) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0L);
        }

        // 2)
        List<TagDto> content = queryFactory
                .select(Projections.bean(TagDto.class,
                        tag.id,
                        tag.name,
                        tag.tagImgUrl
                ))
                .from(tag)
                .where(tag.id.in(ids))
                .orderBy(tag.id.asc())
                .fetch();

        long count = queryFactory
                .selectFrom(tag)
                .fetchCount();

        return new PageImpl<>(content, pageable, count);
    }

    @Override
    public List<TagDto> searchTagAllList() {
        return queryFactory
                .select(Projections.bean(TagDto.class,
                        tag.id,
                        tag.name,
                        tag.tagImgUrl
                ))
                .from(tag)
                .fetch();
    }

    @Override
    public TagDto searchInfoTag(Long id) {
        return queryFactory
                .select(Projections.bean(TagDto.class, tag.id, tag.name, tag.tagImgUrl))
                .from(tag)
                .where(tag.id.eq(id))
                .fetchOne();
    }

    @Override
    public List<TagCountDto> searchTagCountList() {
        List<TagCountDto> existCountList = queryFactory
                .select(Projections.bean(TagCountDto.class, tag.id, tag.name, tag.count().as("count")))
                .from(post)
                .join(post.postTags, postTag)
                .join(postTag.tag, tag)
                .where(post.status.eq(true))
                .groupBy(tag.id, tag.name)
                .orderBy(OrderByNull.DEFAULT)
                .fetch();

        if (existCountList.isEmpty()) {
            List<TagCountDto> notExistCountList = queryFactory
                    .select(Projections.bean(TagCountDto.class, tag.id, tag.name, Expressions.asNumber(0L).as("count")))
                    .from(tag)
                    .fetch();

            if (notExistCountList.isEmpty()) {
                return new ArrayList<>();
            } else {
                return notExistCountList;
            }
        } else {
            List<TagCountDto> notExistCountList = queryFactory
                    .select(Projections.bean(TagCountDto.class, tag.id, tag.name, Expressions.asNumber(0L).as("count")))
                    .from(tag)
                    .where(tag.id.notIn(existCountList.stream().map(TagCountDto::getId).collect(Collectors.toList())))
                    .fetch();

            existCountList.addAll(notExistCountList);

            return existCountList.stream().sorted(Comparator.comparing(TagCountDto::getName)).collect(Collectors.toList());
        }
    }

    @Override
    public List<TagDto> searchFollowerRank() {
        return queryFactory
                .select(Projections.bean(TagDto.class, tag.id, tag.name, tag.tagImgUrl))
                .from(profile)
                .join(profile.tags, tag)
                .join(profile.user, user)
                .where(user.status.ne((byte) 2))
                .groupBy(tag.id, tag.name, tag.tagImgUrl)
                .orderBy(profile.id.count().desc(), tag.name.asc())
                .offset(0)
                .limit(10)
                .fetch();
    }

    @Override
    public List<TagDto> searchPostRank() {
        return queryFactory
                .select(Projections.bean(TagDto.class, tag.id, tag.name, tag.tagImgUrl))
                .from(post)
                .join(post.postTags, postTag)
                .join(postTag.tag, tag)
                .where(post.status.eq(true))
                .groupBy(tag.id, tag.name, tag.tagImgUrl)
                .orderBy(post.id.count().desc(), tag.name.asc())
                .offset(0)
                .limit(10)
                .fetch();
    }

    @Override
    public TagDetailDto searchTagDetail(Long id) {
//        long followerCount = queryFactory
//                .select(profile.id)
//                .from(profile)
//                .join(profile.tags, tag)
//                .join(profile.user, user)
//                .where(tag.id.eq(id), user.status.ne((byte) 2))
//                .fetchCount();
//
//        tagDetailDto.setFollowerCount((int) followerCount);
//
//        long postCount = queryFactory
//                .select(post.id)
//                .from(post)
//                .join(post.postTags, postTag)
//                .join(postTag.tag, tag)
//                .where(tag.id.eq(id), post.status.eq(true))
//                .fetchCount();
//
//        tagDetailDto.setPostCount((int) postCount);

        return queryFactory
                .select(Projections.bean(TagDetailDto.class, tag.id, tag.name, tag.tagImgUrl,
                        tag.profileFollowerCount.as("followerCount"), tag.postTagCount.as("postCount")))
                .from(tag)
                .where(tag.id.eq(id))
                .fetchOne();
    }

}
