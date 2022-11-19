package com.sideproject.codemoim.controller;

import com.sideproject.codemoim.annotation.AccessTokenUse;
import com.sideproject.codemoim.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequestMapping("/api/comment")
@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @AccessTokenUse
    @PostMapping("/create")
    public Long createComment(@RequestBody Map<String, Object> commentInfo, Long userId) {
        return commentService.createComment(commentInfo, userId);
    }

    @AccessTokenUse
    @PatchMapping("/modify")
    public void modifyComment(@RequestBody Map<String, Object> commentInfo, Long userId) {
        commentService.modifyComment(commentInfo, userId);
    }

    @AccessTokenUse
    @DeleteMapping("/delete")
    public Map<String, Object> deleteComment(@RequestBody Map<String, Object> commentInfo, Long userId) {
        return commentService.deleteComment(commentInfo, userId);
    }

    @AccessTokenUse
    @PatchMapping("/select-comment")
    public void selectComment(@RequestBody Map<String, Object> commentInfo, Long userId) {
        commentService.selectComment(commentInfo, userId);
    }

    @AccessTokenUse
    @PostMapping("/vote-comment")
    public boolean voteComment(@RequestBody Map<String, Object> commentInfo, Long userId) {
        return commentService.voteCommentProcess(commentInfo, userId);
    }

    @AccessTokenUse
    @PatchMapping("/vote")
    public void vote(@RequestBody Map<String, Object> commentInfo, Long userId) {
        commentService.voteProcess(commentInfo, userId);
    }

}
