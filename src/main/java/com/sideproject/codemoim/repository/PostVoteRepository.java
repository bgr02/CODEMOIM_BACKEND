package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Post;
import com.sideproject.codemoim.domain.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostVoteRepository extends JpaRepository<PostVote, Long>, CustomPostVoteRepository {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from PostVote pv where pv.post = :post")
    int deletePostVoteByPost(@Param("post") Post post);

}
