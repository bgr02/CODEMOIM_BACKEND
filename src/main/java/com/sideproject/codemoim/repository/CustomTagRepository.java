package com.sideproject.codemoim.repository;

import com.sideproject.codemoim.dto.TagCountDto;
import com.sideproject.codemoim.dto.TagDetailDto;
import com.sideproject.codemoim.dto.TagDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomTagRepository {
    boolean duplicateCheckName(String name);
    Page<TagDto> searchTagList(Pageable pageable);
    TagDto searchInfoTag(Long id);
    List<TagCountDto> searchTagCountList();
    List<TagDto> searchTagAllList();
    List<TagDto> searchFollowerRank();
    List<TagDto> searchPostRank();
    TagDetailDto searchTagDetail(Long id);
}
