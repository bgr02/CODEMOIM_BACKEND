package com.sideproject.codemoim.dto;

import com.sideproject.codemoim.domain.Board;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class BoardListDto {
    private final long id;
    private final String name;
    private final String url;
    private final String icon;
    private final List<BoardListDto> children;

    public BoardListDto(Board board) {
        this.id = board.getId();
        this.name = board.getName();
        this.url = board.getUrl();
        this.icon = board.getIcon();
        this.children = board.getChild().stream().map(BoardListDto::new).collect(Collectors.toList());
    }
}
