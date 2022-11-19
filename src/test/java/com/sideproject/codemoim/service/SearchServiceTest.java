package com.sideproject.codemoim.service;

import com.sideproject.codemoim.dto.PostDto;
import com.sideproject.codemoim.repository.CustomSearchRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @InjectMocks
    SearchService searchService;

    @Mock
    CustomSearchRepository searchRepository;

    @Test
    @DisplayName("검색어를 통한 포스트 조회 성공 테스트")
    void searchPostByKeywordTest_success() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<PostDto> postInfoDto = new PageImpl<>(new ArrayList<>(), pageable, 10);

        given(searchRepository.searchPostByKeyword(any(), anyString())).willReturn(postInfoDto);

        Assertions.assertDoesNotThrow(() -> {
            searchService.searchPostByKeyword(pageable, "test");
        });
    }

    @Test
    @DisplayName("검색어와 게시판 아이디를 통한 포스트 조회 성공 테스트")
    void searchPostByKeywordAndBoardIdTest_success() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<PostDto> postInfoDto = new PageImpl<>(new ArrayList<>(), pageable, 10);

        given(searchRepository.searchPostByKeywordAndBoardId(any(), anyString(), anyLong())).willReturn(postInfoDto);

        Assertions.assertDoesNotThrow(() -> {
            searchService.searchPostByKeywordAndBoardId(pageable, "test", 1L);
        });
    }

    @Test
    @DisplayName("검색어와 태그 이름을 통한 포스트 조회 성공 테스트")
    void searchPostByKeywordAndTagNameTest_success() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<PostDto> postInfoDto = new PageImpl<>(new ArrayList<>(), pageable, 10);

        given(searchRepository.searchPostByKeywordAndTagName(any(), anyString(), anyString())).willReturn(postInfoDto);

        Assertions.assertDoesNotThrow(() -> {
            searchService.searchPostByKeywordAndTagName(pageable, "test", "tag");
        });
    }

}
