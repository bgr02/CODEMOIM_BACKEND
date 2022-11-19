package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.CommentVote;

import java.util.List;

public interface CustomCommentVoteRepository {
    CommentVote searchCommentVoteByCommentIdAndProfileId(Long commentId, Long profileId);
    List<CommentVote> searchCommentVoteByProfileId(Long profileId);
}
