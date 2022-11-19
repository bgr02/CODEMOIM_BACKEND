package com.sideproject.codemoim.controller;

import com.sideproject.codemoim.annotation.AccessTokenUse;
import com.sideproject.codemoim.dto.TagCountDto;
import com.sideproject.codemoim.dto.TagDetailDto;
import com.sideproject.codemoim.dto.TagDto;
import com.sideproject.codemoim.repository.TagRepository;
import com.sideproject.codemoim.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Slf4j
@RequestMapping("/api/tag")
@RestController
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;
    private final TagRepository tagRepository;

    @GetMapping("/duplicate-check-name")
    public boolean duplicateCheckName(@RequestParam("name") String name) throws UnsupportedEncodingException {
        return tagService.duplicateCheckName(name);
    }

    @GetMapping("/list")
    public Page<TagDto> tagList(Pageable pageable) {
        return tagRepository.searchTagList(pageable);
    }

    @GetMapping("/all-list")
    public List<TagDto> tagAllList() {
        return tagRepository.searchTagAllList();
    }

    @GetMapping("/info")
    public TagDto infoTag(@RequestParam("id") Long id) {
        return tagService.searchInfoTag(id);
    }

    @GetMapping("/deletable-check")
    public boolean searchTagDeletableCheck(@RequestParam("id") Long id) {
        return tagService.searchTagDeletableCheck(id);
    }

    @AccessTokenUse
    @PostMapping("/create")
    public void createTag(@RequestBody Map<String, Object> tagInfo, Long userId) {
        tagService.createTag(tagInfo, userId);
    }

    @AccessTokenUse
    @PatchMapping("/modify")
    public void modifyTag(@RequestBody Map<String, Object> tagInfo, Long userId) {
        tagService.modifyTag(tagInfo, userId);
    }

    @AccessTokenUse
    @DeleteMapping("/delete")
    public void deleteTag(@RequestBody Map<String, Object> tagInfo, Long userId) {
        tagService.deleteTag(tagInfo, userId);
    }

    @GetMapping("/count-list")
    public List<TagCountDto> tagCountList() {
        return tagRepository.searchTagCountList();
    }

    @GetMapping("/follower-rank")
    public List<TagDto> searchFollowerRank() {
        return tagService.searchFollowerRank();
    }

    @GetMapping("/post-rank")
    public List<TagDto> searchPostRank() {
        return tagService.searchPostRank();
    }

    @GetMapping("/detail")
    public TagDetailDto searchTagDetail(@RequestParam String name) throws UnsupportedEncodingException {
        return tagService.searchTagDetail(name);
    }

}
