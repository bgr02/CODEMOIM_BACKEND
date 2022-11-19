package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.PostVote;

import java.util.List;

public interface CustomPostVoteRepository {
    PostVote searchPostVoteByPostIdAndProfileId(Long postId, Long profileId);
    List<PostVote> searchPostVoteByProfileId(Long profileId);
}
