package com.sideproject.codemoim.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.codemoim.domain.PostVote;
import com.sideproject.codemoim.domain.QPost;
import com.sideproject.codemoim.domain.QPostVote;
import com.sideproject.codemoim.domain.QProfile;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.sideproject.codemoim.domain.QPost.post;
import static com.sideproject.codemoim.domain.QPostVote.postVote;
import static com.sideproject.codemoim.domain.QProfile.profile;

@RequiredArgsConstructor
public class CustomPostVoteRepositoryImpl implements CustomPostVoteRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public PostVote searchPostVoteByPostIdAndProfileId(Long postId, Long profileId) {
        return queryFactory
                .selectFrom(postVote)
                .join(postVote.post, post)
                .join(postVote.profile, profile)
                .where(post.id.eq(postId), profile.id.eq(profileId))
                .fetchOne();
    }

    @Override
    public List<PostVote> searchPostVoteByProfileId(Long profileId) {
        return queryFactory
                .selectFrom(postVote)
                .join(postVote.profile, profile)
                .where(profile.id.eq(profileId))
                .fetch();
    }
}
