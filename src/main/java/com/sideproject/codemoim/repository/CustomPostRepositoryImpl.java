package com.sideproject.codemoim.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.codemoim.domain.*;
import com.sideproject.codemoim.dto.CommentDto;
import com.sideproject.codemoim.dto.PostDto;
import com.sideproject.codemoim.dto.PostInfoDto;
import com.sideproject.codemoim.dto.PostWithCommentDto;
import com.sideproject.codemoim.util.OrderByNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.sideproject.codemoim.domain.QBoard.board;
import static com.sideproject.codemoim.domain.QComment.comment;
import static com.sideproject.codemoim.domain.QPost.post;
import static com.sideproject.codemoim.domain.QPostTag.postTag;
import static com.sideproject.codemoim.domain.QProfile.profile;
import static com.sideproject.codemoim.domain.QTag.tag;
import static com.sideproject.codemoim.domain.QUser.user;

@RequiredArgsConstructor
public class CustomPostRepositoryImpl implements CustomPostRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Post> searchPostByIdAndStatus(Long id) {
        Post post = queryFactory
                .selectFrom(QPost.post)
                .where(QPost.post.id.eq(id), QPost.post.status.eq(true))
                .fetchOne();

        return Optional.ofNullable(post);
    }

    @Override
    public boolean relationTagExist(Long id) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(post)
                .join(post.postTags, postTag)
                .join(postTag.tag, tag)
                .where(tag.id.eq(id), post.status.eq(true))
                .fetchOne();

        return fetchOne != null;
    }

    @Override
    public PostDto searchPost(Long postId) {
        PostDto postDto = queryFactory
                .select(Projections.bean(PostDto.class, post.id, post.title, post.board.name.as("boardName"), post.board.url.as("boardUrl"),
                        post.board.icon.as("boardIcon"), Expressions.as(Expressions.constant(new ArrayList<String>()), "tagNames"),
                        post.profile.id.as("profileId"), post.profile.username.as("profileName"), post.profile.profileImgUrl, profile.contributionPoint,
                        user.status, post.viewCount, Expressions.asNumber(0).as("commentCount"), post.totalThumbsupVoteCount,
                        post.totalThumbsdownVoteCount, Expressions.asNumber(0).as("scrapCount"), post.createdDate))
                .from(post)
                .join(post.profile, profile)
                .join(profile.user, user)
                .where(post.id.eq(postId))
                .fetchOne();

        List<String> tagNameList = queryFactory
                .select(tag.name)
                .from(post)
                .join(post.postTags, postTag)
                .join(postTag.tag, tag)
                .where(post.id.eq(postId))
                .fetch();

        postDto.setTagNames(tagNameList);

        long commentCount = queryFactory
                .selectFrom(comment)
                .join(comment.post, post)
                .where(post.id.eq(postId))
                .fetchCount();

        postDto.setCommentCount((int) commentCount);

        long scrapCount = getScrapCount(postId);

        postDto.setScrapCount((int) scrapCount);

        return postDto;
    }

    @Override
    public Page<PostDto> searchPostList(Pageable pageable, String type, Long boardId) {
        //BooleanBuilder booleanBuilder = new BooleanBuilder();

        //booleanBuilder.and(post.board.id.eq(boardId));
        //booleanBuilder.and(post.status.eq(true));

        PathBuilder orderByExpression = new PathBuilder(Post.class, "post");
        OrderSpecifier latestOrder = new OrderSpecifier(Order.DESC, orderByExpression.get("id", Long.class));
        OrderSpecifier popularOrder = OrderByNull.DEFAULT;
        OrderSpecifier recommendOrder = OrderByNull.DEFAULT;

        if (type.equals("popular")) {
            //booleanBuilder.and(post.viewCount.ne(0));
            popularOrder = new OrderSpecifier(Order.DESC, orderByExpression.get("viewCount", Integer.class));
        } else if (type.equals("recommend")) {
            //booleanBuilder.and(post.totalThumbsupVoteCount.ne(0));
            recommendOrder = new OrderSpecifier(Order.DESC, orderByExpression.get("totalThumbsupVoteCount", Integer.class));
        }

        List<Long> ids = queryFactory
                .select(post.id)
                .from(post)
                .where(post.board.id.eq(boardId), post.status.eq(true))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(popularOrder, recommendOrder, latestOrder)
                .fetch();

        if (CollectionUtils.isEmpty(ids)) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0L);
        }

        List<PostDto> postDtoList = queryFactory
                .select(Projections.bean(PostDto.class, post.id, post.title, post.board.name.as("boardName"), post.board.url.as("boardUrl"),
                        post.board.icon.as("boardIcon"), Expressions.as(Expressions.constant(new ArrayList<String>()), "tagNames"),
                        post.profile.id.as("profileId"), post.profile.username.as("profileName"), post.profile.profileImgUrl, profile.contributionPoint,
                        user.status, post.viewCount, Expressions.asNumber(0).as("commentCount"), post.totalThumbsupVoteCount,
                        post.totalThumbsdownVoteCount, Expressions.asNumber(0).as("scrapCount"), post.createdDate))
                .from(post)
                .join(post.profile, profile)
                .join(profile.user, user)
                .where(post.id.in(ids))
                .orderBy(popularOrder, recommendOrder, latestOrder)
                .fetch();

        List<PostDto> content = postDtoList.stream().map(postDto -> {
            Long id = postDto.getId();

            List<String> tagNameList = queryFactory
                    .select(tag.name)
                    .from(post)
                    .join(post.postTags, postTag)
                    .join(postTag.tag, tag)
                    .where(post.id.eq(id))
                    .fetch();

            postDto.setTagNames(tagNameList);

            long commentCount = queryFactory
                    .selectFrom(comment)
                    .join(comment.post, post)
                    .where(post.id.eq(id))
                    .fetchCount();

            postDto.setCommentCount((int) commentCount);

            long scrapCount = getScrapCount(id);

            postDto.setScrapCount((int) scrapCount);

            return postDto;
        }).collect(Collectors.toList());

        long count = queryFactory
                .selectFrom(post)
                .where(post.board.id.eq(boardId), post.status.eq(true))
                .fetchCount();

        return new PageImpl<>(content, pageable, count);
    }

    @Override
    public PostInfoDto searchPostInfo(Long postId, Long profileId) {
        PostInfoDto postInfoDto = queryFactory
                .select(Projections.bean(PostInfoDto.class, post.id, post.title, post.content, post.board.name.as("boardName"), post.board.url.as("boardUrl"),
                        post.board.icon.as("boardIcon"), post.board.authority.as("boardAuthority"),
                        Expressions.as(Expressions.constant(new ArrayList<String>()), "tagNames"), post.profile.id.as("profileId"),
                        post.profile.username.as("profileName"), post.profile.profileImgUrl, post.profile.contributionPoint,
                        user.status.as("userStatus"), post.viewCount, Expressions.asNumber(0).as("commentCount"), post.totalThumbsupVoteCount,
                        post.totalThumbsdownVoteCount, Expressions.asNumber(0).as("scrapCount"), post.createdDate,
                        Expressions.as(Expressions.constant(new ArrayList<String>()), "comments"), Expressions.asBoolean(false).as("voteFlag"),
                        Expressions.asNumber(0).as("voteCount")))
                .from(post)
                .join(post.profile, profile)
                .join(profile.user, user)
                .where(post.id.eq(postId), post.status.eq(true))
                .fetchOne();

        if (postInfoDto == null) return null;

        if (profileId != null) {
            PostVote postVote = queryFactory
                    .select(QPostVote.postVote)
                    .from(QPostVote.postVote)
                    .join(QPostVote.postVote.post, post)
                    .join(QPostVote.postVote.profile, profile)
                    .where(post.id.eq(postInfoDto.getId()), profile.id.eq(profileId))
                    .fetchOne();

            if (postVote != null) {
                postInfoDto.setVoteFlag(true);
                postInfoDto.setVoteCount(postVote.getVoteCount());
            } else {
                postInfoDto.setVoteFlag(false);
                postInfoDto.setVoteCount(0);
            }
        }

        List<String> tagNameList = queryFactory
                .select(tag.name)
                .from(post)
                .join(post.postTags, postTag)
                .join(postTag.tag, tag)
                .where(post.id.eq(postId))
                .fetch();

        postInfoDto.setTagNames(tagNameList);

        long commentCount = queryFactory
                .selectFrom(comment)
                .join(comment.post, post)
                .where(post.id.eq(postId))
                .fetchCount();

        postInfoDto.setCommentCount((int) commentCount);

        if (profileId != null) {
            Profile profile = queryFactory
                    .select(QProfile.profile)
                    .from(QProfile.profile)
                    .join(QProfile.profile.scraps, post)
                    .where(QProfile.profile.id.eq(profileId), post.id.eq(postId))
                    .fetchOne();

            if (profile != null) {
                postInfoDto.setScrapFlag(true);
            } else {
                postInfoDto.setScrapFlag(false);
            }
        }

        long scrapCount = getScrapCount(postId);

        postInfoDto.setScrapCount((int) scrapCount);

        List<CommentDto> commentDtoList = queryFactory
                .select(Projections.bean(CommentDto.class, comment.id, comment.content, comment.selectedComment,
                        comment.totalThumbsupVoteCount, comment.totalThumbsdownVoteCount, comment.createdDate,
                        profile.id.as("profileId"), profile.username.as("profileName"), profile.profileImgUrl,
                        profile.contributionPoint, user.status, Expressions.asBoolean(false).as("voteFlag"),
                        Expressions.asNumber(0).as("voteCount")))
                .from(comment)
                .join(comment.post, post)
                .join(comment.profile, profile)
                .join(profile.user, user)
                .where(post.id.eq(postId))
                .orderBy(comment.createdDate.asc())
                .fetch();

        if (!commentDtoList.isEmpty()) {
            boolean flag = false;

            for (CommentDto commentDto : commentDtoList) {
                if (commentDto.getSelectedComment()) {
                    postInfoDto.setSelectedComment(true);
                    break;
                }
            }
        }

        if (profileId != null) {
            commentDtoList = commentDtoList.stream().map(commentDto -> {
                CommentVote commentVote = queryFactory
                        .select(QCommentVote.commentVote)
                        .from(QCommentVote.commentVote)
                        .join(QCommentVote.commentVote.comment, comment)
                        .join(QCommentVote.commentVote.profile, profile)
                        .where(comment.id.eq(commentDto.getId()), profile.id.eq(profileId))
                        .fetchOne();

                if (commentVote != null) {
                    commentDto.setVoteFlag(true);
                    commentDto.setVoteCount(commentVote.getVoteCount());
                } else {
                    commentDto.setVoteFlag(false);
                    commentDto.setVoteCount(0);
                }

                return commentDto;
            }).collect(Collectors.toList());
        }

        postInfoDto.setComments(commentDtoList);

        return postInfoDto;
    }

    @Override
    public Optional<Post> searchPostByIdAndProfileIdAndStatus(long postId, long profileId) {
        Post post = queryFactory
                .selectFrom(QPost.post)
                .join(QPost.post.profile, profile)
                .where(QPost.post.id.eq(postId), profile.id.eq(profileId))
                .fetchOne();

        return Optional.ofNullable(post);
    }

    @Override
    public long getScrapCount(Long id) {
        return queryFactory
                .selectFrom(profile)
                .join(profile.user, user)
                .join(profile.scraps, post)
                .where(post.id.eq(id), user.status.ne((byte) 2))
                .fetchCount();
    }

    @Override
    public boolean postExist() {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(post)
                .where(post.status.eq(true))
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public Page<PostDto> searchTagPost(Pageable pageable, String type, Long tagId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.and(tag.id.eq(tagId));
        booleanBuilder.and(post.status.eq(true));

        PathBuilder orderByExpression = new PathBuilder(Post.class, "post");
        OrderSpecifier latestOrder = OrderByNull.DEFAULT;
        OrderSpecifier popularOrder = OrderByNull.DEFAULT;
        OrderSpecifier recommendOrder = OrderByNull.DEFAULT;

        if (type.equals("latest")) {
            latestOrder = new OrderSpecifier(Order.DESC, orderByExpression.get("id", Long.class));
        } else if (type.equals("popular")) {
            booleanBuilder.and(post.viewCount.ne(0));
            popularOrder = new OrderSpecifier(Order.DESC, orderByExpression.get("viewCount", Integer.class));
        } else if (type.equals("recommend")) {
            booleanBuilder.and(post.totalThumbsupVoteCount.ne(0));
            recommendOrder = new OrderSpecifier(Order.DESC, orderByExpression.get("totalThumbsupVoteCount", Integer.class));
        }

        List<Long> ids = queryFactory
                .select(post.id)
                .from(post)
                .join(post.postTags, postTag)
                .join(postTag.tag, tag)
                .where(booleanBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(popularOrder, recommendOrder, latestOrder)
                .fetch();

        if (CollectionUtils.isEmpty(ids)) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0L);
        }

        List<PostDto> postDtoList = queryFactory
                .select(Projections.bean(PostDto.class, post.id, post.title, post.board.name.as("boardName"), post.board.url.as("boardUrl"),
                        post.board.icon.as("boardIcon"), Expressions.as(Expressions.constant(new ArrayList<String>()), "tagNames"),
                        post.profile.id.as("profileId"), post.profile.username.as("profileName"), post.profile.profileImgUrl, profile.contributionPoint,
                        user.status, post.viewCount, Expressions.asNumber(0).as("commentCount"), post.totalThumbsupVoteCount,
                        post.totalThumbsdownVoteCount, Expressions.asNumber(0).as("scrapCount"), post.createdDate))
                .from(post)
                .join(post.profile, profile)
                .join(profile.user, user)
                .where(post.id.in(ids))
                .orderBy(popularOrder, recommendOrder, latestOrder)
                .fetch();

        List<PostDto> content = postDtoList.stream().map(postDto -> {
            Long id = postDto.getId();

            List<String> tagNameList = queryFactory
                    .select(tag.name)
                    .from(post)
                    .join(post.postTags, postTag)
                    .join(postTag.tag, tag)
                    .where(post.id.eq(id))
                    .fetch();

            postDto.setTagNames(tagNameList);

            long commentCount = queryFactory
                    .selectFrom(comment)
                    .join(comment.post, post)
                    .where(post.id.eq(id))
                    .fetchCount();

            postDto.setCommentCount((int) commentCount);

            long scrapCount = getScrapCount(id);

            postDto.setScrapCount((int) scrapCount);

            return postDto;
        }).collect(Collectors.toList());

        long count = queryFactory
                .selectFrom(post)
                .join(post.postTags, postTag)
                .join(postTag.tag, tag)
                .where(tag.id.eq(tagId), post.status.eq(true))
                .fetchCount();

        return new PageImpl<>(content, pageable, count);
    }

    @Override
    public List<PostDto> searchDashboardFixedPostList(String type) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        booleanBuilder.and(post.status.eq(true));
        booleanBuilder.and(post.board.authority.ne("ADMIN"));

        PathBuilder orderByExpression = new PathBuilder(Post.class, "post");
        OrderSpecifier postOrder = null;

        if (type.equals("popular")) {
            booleanBuilder.and(post.viewCount.ne(0));
            postOrder = new OrderSpecifier(Order.DESC, orderByExpression.get("viewCount", Integer.class));
        } else if (type.equals("recommend")) {
            booleanBuilder.and(post.totalThumbsupVoteCount.ne(0));
            postOrder = new OrderSpecifier(Order.DESC, orderByExpression.get("totalThumbsupVoteCount", Integer.class));
        }

        List<Long> ids = queryFactory
                .select(post.id)
                .from(post)
                .join(post.board, board)
                .where(booleanBuilder)
                .offset(0)
                .limit(5)
                .orderBy(postOrder)
                .fetch();

        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }

        return queryFactory
                .select(Projections.bean(PostDto.class, post.id, post.title, post.board.name.as("boardName"), post.board.url.as("boardUrl"),
                        post.board.icon.as("boardIcon"), post.profile.id.as("profileId"), post.profile.username.as("profileName"),
                        post.profile.profileImgUrl, user.status, post.createdDate))
                .from(post)
                .join(post.profile, profile)
                .join(profile.user, user)
                .where(post.id.in(ids))
                .orderBy(postOrder)
                .fetch();
    }

    @Override
    public List<Map<String, Object>> searchDashboardNonFixedPostList() {
        List<Long> boardList = queryFactory
                .select(board.id)
                .from(board)
                .where(board.status.eq(true), board.display.eq(true))
                .orderBy(board.sortDisplay.asc())
                .fetch();

        if (!boardList.isEmpty()) {
            List<Map<String, Object>> boardPostList = new ArrayList<>();

            for (Long boardId : boardList) {
                Map<String, Object> boardPostInfo = new HashMap<>();

                List<Long> ids = queryFactory
                        .select(post.id)
                        .from(post)
                        .where(post.board.id.eq(boardId), post.status.eq(true))
                        .offset(0)
                        .limit(5)
                        .orderBy(post.id.desc())
                        .fetch();

                Board board = queryFactory
                        .selectFrom(QBoard.board)
                        .where(QBoard.board.id.eq(boardId))
                        .fetchOne();

                boardPostInfo.put("boardName", board.getName());
                boardPostInfo.put("boardUrl", board.getUrl());
                boardPostInfo.put("boardIcon", board.getIcon());

                if (CollectionUtils.isEmpty(ids)) {
                    boardPostList.add(boardPostInfo);

                    continue;
                }

                List<PostDto> postDtoList = queryFactory
                        .select(Projections.bean(PostDto.class, post.id, post.title, post.board.name.as("boardName"), post.board.url.as("boardUrl"),
                                post.board.icon.as("boardIcon"), post.profile.id.as("profileId"), post.profile.username.as("profileName"),
                                post.profile.profileImgUrl, user.status, post.createdDate))
                        .from(post)
                        .join(post.profile, profile)
                        .join(post.profile, profile)
                        .join(profile.user, user)
                        .where(post.id.in(ids))
                        .orderBy(post.id.desc())
                        .fetch();

                boardPostInfo.put("postList", postDtoList);

                boardPostList.add(boardPostInfo);
            }

            return boardPostList;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Profile> searchScrapUserByPostId(Long id) {
        return queryFactory
                .selectFrom(profile)
                .join(profile.user, user)
                .join(profile.scraps, post)
                .where(post.id.eq(id), user.status.ne((byte) 2))
                .fetch();
    }

    @Override
    public Page<PostDto> searchPostListByProfileId(Pageable pageable, Long id) {
        // 1) 커버링 인덱스로 대상 조회
        List<Long> ids = queryFactory
                .select(post.id)
                .from(post)
                .join(post.profile, profile)
                .where(profile.id.eq(id), post.status.eq(true))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.id.asc())
                .fetch();

        if (CollectionUtils.isEmpty(ids)) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0L);
        }

        List<PostDto> postDtoList = queryFactory
                .select(Projections.bean(PostDto.class, post.id, post.title, post.board.name.as("boardName"), post.board.url.as("boardUrl"),
                        post.board.icon.as("boardIcon"), Expressions.as(Expressions.constant(new ArrayList<String>()), "tagNames"),
                        post.profile.id.as("profileId"), post.profile.username.as("profileName"), post.profile.profileImgUrl, profile.contributionPoint,
                        user.status, post.viewCount, Expressions.asNumber(0).as("commentCount"), post.totalThumbsupVoteCount,
                        post.totalThumbsdownVoteCount, Expressions.asNumber(0).as("scrapCount"), post.createdDate))
                .from(post)
                .join(post.profile, profile)
                .join(profile.user, user)
                .where(post.id.in(ids))
                .orderBy(post.id.asc())
                .fetch();

        List<PostDto> content = postDtoList.stream().map(postDto -> {
            Long postId = postDto.getId();

            List<String> tagNameList = queryFactory
                    .select(tag.name)
                    .from(post)
                    .join(post.postTags, postTag)
                    .join(postTag.tag, tag)
                    .where(post.id.eq(postId))
                    .fetch();

            postDto.setTagNames(tagNameList);

            long commentCount = queryFactory
                    .selectFrom(comment)
                    .join(comment.post, post)
                    .where(post.id.eq(postId))
                    .fetchCount();

            postDto.setCommentCount((int) commentCount);

            long scrapCount = getScrapCount(postId);

            postDto.setScrapCount((int) scrapCount);

            return postDto;
        }).collect(Collectors.toList());

        long count = queryFactory
                .selectFrom(post)
                .join(post.profile, profile)
                .where(profile.id.eq(id), post.status.eq(true))
                .fetchCount();

        return new PageImpl<>(content, pageable, count);
    }

    @Override
    public Page<PostWithCommentDto> searchCommentPostListByProfileId(Pageable pageable, Long id) {
        List<Long> ids = queryFactory
                .select(comment.id)
                .from(comment)
                .join(comment.profile, QProfile.profile)
                .where(QProfile.profile.id.eq(id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (CollectionUtils.isEmpty(ids)) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0L);
        }

        List<PostWithCommentDto> postWithCommentDtoList = queryFactory
                .select(Projections.bean(PostWithCommentDto.class, post.id, post.title, post.board.name.as("boardName"), post.board.url.as("boardUrl"),
                        post.board.icon.as("boardIcon"), Expressions.as(Expressions.constant(new ArrayList<String>()), "tagNames"),
                        post.profile.id.as("profileId"), post.profile.username.as("profileName"), post.profile.profileImgUrl, QProfile.profile.contributionPoint,
                        user.status, post.viewCount, Expressions.asNumber(0).as("commentCount"), post.totalThumbsupVoteCount,
                        post.totalThumbsdownVoteCount, Expressions.asNumber(0).as("scrapCount"), post.createdDate, comment.id.as("commentId")))
                .from(comment)
                .join(comment.post, post)
                .join(post.profile, profile)
                .join(profile.user, user)
                .where(comment.id.in(ids))
                .orderBy(post.id.asc())
                .fetch();

        List<PostWithCommentDto> content = postWithCommentDtoList.stream().map(postWithCommentDto -> {
            Long postId = postWithCommentDto.getId();

            List<String> tagNameList = queryFactory
                    .select(tag.name)
                    .from(post)
                    .join(post.postTags, postTag)
                    .join(postTag.tag, tag)
                    .where(post.id.eq(postId))
                    .fetch();

            postWithCommentDto.setTagNames(tagNameList);

            long commentCount = queryFactory
                    .selectFrom(comment)
                    .join(comment.post, post)
                    .where(post.id.eq(postId))
                    .fetchCount();

            postWithCommentDto.setCommentCount((int) commentCount);

            long scrapCount = getScrapCount(postId);

            postWithCommentDto.setScrapCount((int) scrapCount);

            return postWithCommentDto;
        }).collect(Collectors.toList());

        long count = queryFactory
                .selectFrom(comment)
                .join(comment.profile, QProfile.profile)
                .where(QProfile.profile.id.eq(id))
                .fetchCount();

        return new PageImpl<>(content, pageable, count);
    }

    @Override
    public Page<PostDto> searchScrapListByProfileId(Pageable pageable, Long id) {
//        Profile profile = queryFactory
//                .selectFrom(QProfile.profile)
//                .where(QProfile.profile.id.eq(id))
//                .fetchOne();

        List<Long> ids = queryFactory
                .select(post.id)
                .from(profile)
                .join(profile.scraps, post)
                .where(post.status.eq(true), QProfile.profile.id.eq(id))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.id.desc())
                .fetch();

        //Set<Post> scraps = profile.getScraps().stream().filter(Post::getStatus).collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(ids)) {
            return new PageImpl<>(new ArrayList<>(), pageable, 0L);
        }
//        else {
//            if (totalScrapCount > 5) {
//                ids = scraps.stream().sorted(Comparator.comparing(Post::getId)).skip(pageable.getOffset()).limit(pageable.getPageSize())
//                        .map(Post::getId).collect(Collectors.toList());
//            } else if (totalScrapCount > 0) {
//                ids = scraps.stream().sorted(Comparator.comparing(Post::getId)).map(Post::getId).collect(Collectors.toList());
//            }
//        }

        List<PostDto> postDtoList = queryFactory
                .select(Projections.bean(PostDto.class, post.id, post.title, post.board.name.as("boardName"), post.board.url.as("boardUrl"),
                        post.board.icon.as("boardIcon"), Expressions.as(Expressions.constant(new ArrayList<String>()), "tagNames"),
                        post.profile.id.as("profileId"), post.profile.username.as("profileName"), post.profile.profileImgUrl, QProfile.profile.contributionPoint,
                        user.status, post.viewCount, Expressions.asNumber(0).as("commentCount"), post.totalThumbsupVoteCount,
                        post.totalThumbsdownVoteCount, Expressions.asNumber(0).as("scrapCount"), post.createdDate))
                .from(post)
                .join(post.profile, QProfile.profile)
                .join(QProfile.profile.user, user)
                .where(post.id.in(ids))
                .orderBy(post.id.desc())
                .fetch();

        List<PostDto> content = postDtoList.stream().map(postDto -> {
            Long postId = postDto.getId();

            List<String> tagNameList = queryFactory
                    .select(tag.name)
                    .from(post)
                    .join(post.postTags, postTag)
                    .join(postTag.tag, tag)
                    .where(post.id.eq(postId))
                    .fetch();

            postDto.setTagNames(tagNameList);

            long commentCount = queryFactory
                    .selectFrom(comment)
                    .join(comment.post, post)
                    .where(post.id.eq(postId))
                    .fetchCount();

            postDto.setCommentCount((int) commentCount);

            long scrapCount = getScrapCount(postId);

            postDto.setScrapCount((int) scrapCount);

            return postDto;
        }).collect(Collectors.toList());

        long totalScrapCount = queryFactory
                .select(post.id)
                .from(profile)
                .join(profile.scraps, post)
                .where(post.status.eq(true), QProfile.profile.id.eq(id))
                .fetchCount();

        return new PageImpl<>(content, pageable, totalScrapCount);
    }

}
