package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Comment;

public interface CustomCommentRepository {
    Comment searchCommentByCommentId(Long commentId);
}
