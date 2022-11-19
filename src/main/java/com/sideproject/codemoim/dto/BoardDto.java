package com.sideproject.codemoim.dto;

import com.sideproject.codemoim.domain.Board;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardDto {
    private long id;
    private String name;
    private Boolean status;
    private long parent_id;
    private String url;
    private String icon;
    private int sort;
    private Boolean display;
    private Long sortDisplay;
    private String authority;
    private boolean subBoardExist;

    public BoardDto(Board board) {
        this.id = board.getId();
        this.name = board.getName();
        this.status = board.getStatus();
        this.parent_id = board.getParent() != null ? board.getParent().getId() : 0L;
        this.url = board.getUrl();
        this.icon = board.getIcon();
        this.sort = board.getSort();
        this.display = board.getDisplay();
        this.sortDisplay = board.getSortDisplay();
        this.authority = board.getAuthority();
    }
}
