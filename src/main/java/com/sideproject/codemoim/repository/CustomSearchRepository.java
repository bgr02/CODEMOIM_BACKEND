package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.dto.PostDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomSearchRepository {
    void buildSearchIndex() throws InterruptedException;
    Page<PostDto>  searchPostByKeyword(Pageable pageable, String keyword);
    Page<PostDto> searchPostByKeywordAndBoardId(Pageable pageable, String keyword, Long boardId);
    Page<PostDto> searchPostByKeywordAndTagName(Pageable pageable, String keyword, String tagName);
    Page<PostDto> searchPostByKeywordUseLike(Pageable pageable, String searchKeyword);
}
