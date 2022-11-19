package com.sideproject.codemoim.controller;

import com.sideproject.codemoim.annotation.AccessTokenUse;
import com.sideproject.codemoim.dto.BoardDto;
import com.sideproject.codemoim.dto.BoardListDto;
import com.sideproject.codemoim.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/board")
@RestController
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/tree-list")
    public List<BoardListDto> searchTreeList(@RequestParam("type") String type) {
        return boardService.searchTreeList(type);
    }

    @GetMapping("/info")
    public BoardDto searchBoardInfo(@RequestParam("id") Long id) {
        return boardService.searchBoardInfo(id);
    }

    @GetMapping("/deletable-check")
    public boolean searchBoardDeletableCheck(@RequestParam("id") Long id) {
        return boardService.searchBoardDeletableCheck(id);
    }

    @GetMapping("/all-list")
    public List<BoardDto> searchAllList() {
        return boardService.searchAllList();
    }

    @GetMapping("/sub-list")
    public List<BoardDto> searchSubList() throws InterruptedException {
        return boardService.searchSubList();
    }

    @AccessTokenUse
    @PostMapping("/create")
    public void createBoard(@RequestBody Map<String, Object> boardInfo, Long userId) {
        boardService.createBoard(boardInfo, userId);
    }

    @AccessTokenUse
    @PutMapping("/modify")
    public void modifyBoard(@RequestBody Map<String, Object> boardInfo, Long userId) {
        boardService.modifyBoard(boardInfo, userId);
    }

    @AccessTokenUse
    @DeleteMapping("/delete")
    public void deleteBoard(@RequestBody Map<String, Object> boardInfo, Long userId) {
        boardService.deleteBoard(boardInfo, userId);
    }

}
