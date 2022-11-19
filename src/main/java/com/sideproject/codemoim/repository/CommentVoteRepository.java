package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Comment;
import com.sideproject.codemoim.domain.CommentVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentVoteRepository extends JpaRepository<CommentVote, Long>, CustomCommentVoteRepository {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from CommentVote cv where cv.comment = :comment")
    int deleteCommentVoteByComment(@Param("comment") Comment comment);

}
