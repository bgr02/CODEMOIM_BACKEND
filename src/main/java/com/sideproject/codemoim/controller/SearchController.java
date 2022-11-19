package com.sideproject.codemoim.controller;

import com.sideproject.codemoim.annotation.AccessTokenUse;
import com.sideproject.codemoim.dto.PostDto;
import com.sideproject.codemoim.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;

    @AccessTokenUse
    @GetMapping("/init")
    public void initializeSearchIndex(Long userId) throws InterruptedException {
        searchService.initializeSearchIndex(userId);
    }

    @GetMapping("/post-keyword")
    public Page<PostDto> searchPost(Pageable pageable, @RequestParam String keyword) throws UnsupportedEncodingException {
        return searchService.searchPostByKeyword(pageable, keyword);
    }

    @GetMapping("/post-keyword-boardId")
    public Page<PostDto> searchPostByKeywordAndBoardId(Pageable pageable, @RequestParam String keyword, @RequestParam Long boardId) throws UnsupportedEncodingException {
        return searchService.searchPostByKeywordAndBoardId(pageable, keyword, boardId);
    }

    @GetMapping("/post-keyword-tagName")
    public Page<PostDto> searchPostByKeywordAndTagName(Pageable pageable, @RequestParam String keyword, @RequestParam String tagName) throws UnsupportedEncodingException {
        return searchService.searchPostByKeywordAndTagName(pageable, keyword, tagName);
    }

}
