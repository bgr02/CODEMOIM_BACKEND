package com.sideproject.codemoim.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.codemoim.domain.Post;
import com.sideproject.codemoim.domain.PostTag;
import com.sideproject.codemoim.domain.Tag;
import com.sideproject.codemoim.dto.PostDto;
import lombok.RequiredArgsConstructor;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.schema.management.SearchSchemaManager;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.sideproject.codemoim.domain.QPost.*;

@Repository
@RequiredArgsConstructor
public class CustomSearchRepositoryImpl implements CustomSearchRepository {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Override
    public void buildSearchIndex() throws InterruptedException {
        SearchSession searchSession = Search.session(entityManager);

        SearchSchemaManager schemaManager = searchSession.schemaManager();
        schemaManager.dropAndCreate();

        List<Class<?>> entityList = new ArrayList<>();

        entityList.add(Post.class);
        entityList.add(PostTag.class);

        MassIndexer indexer = searchSession
                .massIndexer(entityList)
                .batchSizeToLoadObjects(1)
                .idFetchSize(Integer.MIN_VALUE);

        indexer.startAndWait();
    }

    @Override
    public Page<PostDto> searchPostByKeyword(Pageable pageable, String keyword) {
        SearchSession searchSession = Search.session(entityManager);

        //f.bool().must(f.wildcard().fields("title", "content").matching("*" + keyword + "*"))
        SearchResult<Post> result = searchSession.search(Post.class)
                .where(f -> f.bool()
                        .must(f.wildcard()
                                .fields("title", "content")
                                .matching("*" + keyword + "*"))
                        .must(f.match().field("status").matching(true))
                )
                .sort(f -> f.field("createdDate").desc())
                .fetch((int) pageable.getOffset(), pageable.getPageSize());

        List<Post> postList = result.hits();

        long totalCount = searchSession.search(Post.class)
                .where(f -> f.bool()
                        .must(f.wildcard()
                                .fields("title", "content")
                                .matching("*" + keyword + "*"))
                        .must(f.match().field("status").matching(true))
                )
                .fetchTotalHitCount();

        if (!postList.isEmpty()) {
            List<PostDto> content = postList.stream().map(post -> postRepository.searchPost(post.getId())).collect(Collectors.toList());

            return new PageImpl<>(content, pageable, totalCount);
        } else {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    @Override
    public Page<PostDto> searchPostByKeywordAndBoardId(Pageable pageable, String keyword, Long boardId) {
        SearchSession searchSession = Search.session(entityManager);

        SearchResult<Post> result = searchSession.search(Post.class)
                .where(f -> f.bool()
                        .must(f.match().field("board.id").matching(boardId))
                        .must(f.wildcard().fields("title", "content").matching("*" + keyword + "*"))
                        .must(f.match().field("status").matching(true))
                )
                .sort(f -> f.field("createdDate").desc())
                .fetch((int) pageable.getOffset(), pageable.getPageSize());

        List<Post> postList = result.hits();

        long totalCount = searchSession.search(Post.class)
                .where(f -> f.bool()
                        .must(f.match().field("board.id").matching(boardId))
                        .must(f.wildcard().fields("title", "content").matching("*" + keyword + "*"))
                        .must(f.match().field("status").matching(true))
                )
                .fetchTotalHitCount();

        if (!postList.isEmpty()) {
            List<PostDto> content = postList.stream().map(post -> postRepository.searchPost(post.getId())).collect(Collectors.toList());

            return new PageImpl<>(content, pageable, totalCount);
        } else {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    @Override
    public Page<PostDto> searchPostByKeywordAndTagName(Pageable pageable, String keyword, String tagName) {
        Tag tag = tagRepository.findByName(tagName);

        SearchSession searchSession = Search.session(entityManager);

        SearchResult<PostTag> result = searchSession.search(PostTag.class)
                .where(f -> f.bool()
                        .must(f.match().field("tag.id").matching(tag.getId()))
                        //.minimumShouldMatchNumber(1)
                        .must(f.wildcard().fields("post.title", "post.content").matching("*" + keyword + "*"))
                        .must(f.match().field("post.status").matching(true))
                )
                .sort(f -> f.field("post.createdDate").desc())
                .fetch((int) pageable.getOffset(), pageable.getPageSize());

        List<PostTag> postList = result.hits();

        long totalCount = searchSession.search(PostTag.class)
                .where(f -> f.bool()
                        .must(f.match().field("tag.id").matching(tag.getId()))
                        .must(f.wildcard().fields("post.title", "post.content").matching("*" + keyword + "*"))
                        .must(f.match().field("post.status").matching(true))
                )
                .fetchTotalHitCount();

        if (!postList.isEmpty()) {
            List<PostDto> content = postList.stream().map(postTag -> postRepository.searchPost(postTag.getPost().getId())).collect(Collectors.toList());

            return new PageImpl<>(content, pageable, totalCount);
        } else {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

    @Override
    public Page<PostDto> searchPostByKeywordUseLike(Pageable pageable, String searchKeyword) {
        List<Post> postList = jpaQueryFactory().selectFrom(post)
                .where((post.title.contains(searchKeyword).or(post.content.contains(searchKeyword))).and(post.status.eq(true)))
                .fetch();

        long totalCount = jpaQueryFactory().selectFrom(post)
                .where((post.title.contains(searchKeyword).or(post.content.contains(searchKeyword))).and(post.status.eq(true)))
                .fetchCount();

        if (!postList.isEmpty()) {
            List<PostDto> content = postList.stream().map(post -> postRepository.searchPost(post.getId())).collect(Collectors.toList());

            return new PageImpl<>(content, pageable, totalCount);
        } else {
            return new PageImpl<>(new ArrayList<>(), pageable, 0);
        }
    }

}
