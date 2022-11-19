package com.sideproject.codemoim.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.codemoim.domain.Comment;
import lombok.RequiredArgsConstructor;

import static com.sideproject.codemoim.domain.QComment.comment;
import static com.sideproject.codemoim.domain.QPost.post;
import static com.sideproject.codemoim.domain.QProfile.profile;

@RequiredArgsConstructor
public class CustomCommentRepositoryImpl implements CustomCommentRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Comment searchCommentByCommentId(Long commentId) {
        return queryFactory
                .selectFrom(comment)
                .where(comment.id.eq(commentId))
                .join(comment.post, post)
                .fetchJoin()
                .join(post.profile, profile)
                .fetchJoin()
                .fetchOne();
    }

}
