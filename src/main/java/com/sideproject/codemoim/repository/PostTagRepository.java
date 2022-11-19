package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Post;
import com.sideproject.codemoim.domain.PostTag;
import com.sideproject.codemoim.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    PostTag findByPostAndTag(Post post, Tag tag);
}
