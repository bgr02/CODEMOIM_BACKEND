package com.sideproject.codemoim.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    @GenericField(projectable = Projectable.NO, sortable = Sortable.YES)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Board parent;
    @OneToMany(mappedBy = "parent")
    private List<Board> child = new ArrayList<>();
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private Boolean status;
    @Column(nullable = false)
    private String url;
    @Column(nullable = false)
    private String icon;
    @Column(nullable = false)
    private Integer sort;
    @Column(nullable = false)
    private Boolean display;
    @Column
    private Long sortDisplay;
    @Column(nullable = false)
    private String authority;
    @OneToMany(mappedBy = "board")
    private List<Post> posts = new ArrayList<>();

    @Builder
    public Board(Board parent, String name, Boolean status, String url, String icon, Integer sort, Boolean display, Long sortDisplay, String authority) {
        this.parent = parent;
        this.name = name;
        this.status = status;
        this.url = url;
        this.icon = icon;
        this.sort = sort;
        this.display = display;
        this.sortDisplay = sortDisplay;
        this.authority = authority;
    }

    public void updateParent(Board board) {
        this.parent = board;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateStatus(boolean status) {
        this.status = status;
    }

    public void updateUrl(String url) {
        this.url = url;
    }

    public void updateIcon(String icon) {
        this.icon = icon;
    }

    public void updateSort(int sort) {
        this.sort = sort;
    }

    public Long getSortDisplay() {
        return sortDisplay == null ? 0 : sortDisplay;
    }

    public void updateDisplay(boolean display) {
        this.display = display;
    }

    public void updateSortDisplay(Long sortDisplay) {
        this.sortDisplay = sortDisplay;
    }

    public void updateAuthority(String authority) {
        this.authority = authority;
    }
}
