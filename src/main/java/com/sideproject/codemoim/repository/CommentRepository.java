package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CustomCommentRepository {

}
