package com.sideproject.codemoim.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sideproject.codemoim.domain.Board;
import com.sideproject.codemoim.domain.QBoard;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static com.sideproject.codemoim.domain.QBoard.board;
import static com.sideproject.codemoim.domain.QPost.post;

@RequiredArgsConstructor
public class CustomBoardRepositoryImpl implements CustomBoardRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Board> searchByIdAndStatus(Long id) {
        Board board = queryFactory
                .selectFrom(QBoard.board)
                .where(QBoard.board.id.eq(id), QBoard.board.status.eq(true))
                .fetchOne();

        return Optional.ofNullable(board);
    }

    @Override
    public List<Board> searchTreeList(String type) {
        QBoard parent = new QBoard("parent");
        QBoard child = new QBoard("child");

        BooleanBuilder builder = new BooleanBuilder();

        builder.and(parent.parent.isNull());

        if (type.equals("status")) {
            builder.and(parent.status.eq(true));
            builder.and(child.status.eq(true));
        }

        return queryFactory
                .selectFrom(parent)
                .distinct()
                .leftJoin(parent.child, child)
                .fetchJoin()
                .where(builder)
                .orderBy(parent.sort.asc(), child.sort.asc())
                .fetch();
    }

    @Override
    public boolean subBoardExist(Long id) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(board)
                .where(board.parent.id.eq(id), board.status.eq(true))
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public boolean postExist(Long id) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(post)
                .where(post.board.id.eq(id), post.status.eq(true))
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public List<Board> searchSubList() {
        QBoard parent = new QBoard("parent");
        QBoard child = new QBoard("child");

//        select
//        *
//        from board
//        where board_id not in (select distinct parent_id from board where parent_id is not null);
        return queryFactory
                .selectFrom(child)
                .where(child.id.notIn(
                        JPAExpressions.select(parent.parent.id).from(parent).where(parent.parent.id.isNotNull(), parent.status.eq(true))), child.status.eq(true)
                )
                .fetch();
    }

}
