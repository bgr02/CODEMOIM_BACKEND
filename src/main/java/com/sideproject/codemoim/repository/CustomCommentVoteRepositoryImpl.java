package com.sideproject.codemoim.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.codemoim.domain.CommentVote;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.sideproject.codemoim.domain.QComment.comment;
import static com.sideproject.codemoim.domain.QCommentVote.commentVote;
import static com.sideproject.codemoim.domain.QProfile.profile;

@RequiredArgsConstructor
public class CustomCommentVoteRepositoryImpl implements CustomCommentVoteRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public CommentVote searchCommentVoteByCommentIdAndProfileId(Long commentId, Long profileId) {
        return queryFactory
                .selectFrom(commentVote)
                .join(commentVote.comment, comment)
                .join(commentVote.profile, profile)
                .where(comment.id.eq(commentId), profile.id.eq(profileId))
                .fetchOne();
    }

    @Override
    public List<CommentVote> searchCommentVoteByProfileId(Long profileId) {
        return queryFactory
                .selectFrom(commentVote)
                .join(commentVote.profile, profile)
                .where(profile.id.eq(profileId))
                .fetch();
    }

}
