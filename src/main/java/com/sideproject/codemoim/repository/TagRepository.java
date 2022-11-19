package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.domain.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long>, CustomTagRepository {
    Tag findByName(String name);
}
