package com.sideproject.codemoim.controller;

import com.sideproject.codemoim.annotation.AccessTokenUse;
import com.sideproject.codemoim.dto.PostDto;
import com.sideproject.codemoim.dto.PostInfoDto;
import com.sideproject.codemoim.dto.PostWithCommentDto;
import com.sideproject.codemoim.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PatchMapping("/modify-view-count")
    public void modifyViewCount(@RequestBody Map<String, Object> postInfo) {
        postService.modifyViewCount(postInfo);
    }

    @GetMapping("/list")
    public Page<PostDto> postList(Pageable pageable, @RequestParam String type, @RequestParam Long boardId) {
        return postService.searchPostList(pageable, type, boardId);
    }

    @AccessTokenUse
    @PostMapping("/create")
    public Long createPost(@RequestBody Map<String, Object> postInfo, Long userId) {
        return postService.createPost(postInfo, userId);
    }

    @AccessTokenUse
    @PutMapping("/modify")
    public void modifyPost(@RequestBody Map<String, Object> postInfo, Long userId) {
        postService.modifyPost(postInfo, userId);
    }

    @AccessTokenUse
    @DeleteMapping("/delete")
    public void deletePost(@RequestBody Map<String, Object> deleteInfo, Long userId) {
        postService.deletePost(deleteInfo, userId);
    }

    @GetMapping("/info")
    public PostInfoDto infoPost(@RequestParam("postId") Long postId, @RequestParam(name = "profileId", required = false) Long profileId) {
        return postService.searchInfoPost(postId, profileId);
    }

    @AccessTokenUse
    @PostMapping("/vote-post")
    public boolean votPostProcess(@RequestBody Map<String, Object> voteInfo, Long userId) {
        return postService.votePostProcess(voteInfo, userId);
    }

    @AccessTokenUse
    @PatchMapping("/vote")
    public void votePost(@RequestBody Map<String, Object> voteInfo, Long userId) {
        postService.voteProcess(voteInfo, userId);
    }

    @AccessTokenUse
    @PostMapping("/scrap")
    public boolean scrapPost(@RequestBody Map<String, Object> scrapInfo, Long userId) {
        return postService.scrapPost(scrapInfo, userId);
    }

    @GetMapping("/tag-post")
    public Page<PostDto> searchTagPost(Pageable pageable, @RequestParam String type, @RequestParam String name) throws UnsupportedEncodingException {
        return postService.searchTagPost(pageable, type, name);
    }

    @GetMapping("/dashboard-fixed-list")
    public List<PostDto> searchDashboardFixedPostList(@RequestParam String type) {
        return postService.searchDashboardFixedPostList(type);
    }

    @GetMapping("/dashboard-non-fixed-list")
    public List<Map<String, Object>> searchDashboardNonFixedPostList() {
        return postService.searchDashboardNonFixedPostList();
    }

    @GetMapping("/post-list-by-profile")
    public Page<PostDto> searchPostListByProfileId(Pageable pageable, @RequestParam Long id) {
        return postService.searchPostListByProfileId(pageable, id);
    }

    @GetMapping("/comment-post-list-by-profile")
    public Page<PostWithCommentDto> searchCommentPostListByProfileId(Pageable pageable, @RequestParam Long id) {
        return postService.searchCommentPostListByProfileId(pageable, id);
    }

    @GetMapping("/scrap-list-by-profile")
    public Page<PostDto> searchScrapListByProfileId(Pageable pageable, @RequestParam Long id) {
        return postService.searchScrapListByProfileId(pageable, id);
    }

}
