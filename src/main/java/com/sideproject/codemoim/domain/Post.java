package com.sideproject.codemoim.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Indexed
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@Column(name = "post_id", columnDefinition = "UNSIGNED bigint(20)")
    @Column(name = "post_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id")
    @IndexedEmbedded(includePaths = "id")
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id")
    private Profile profile;
    @Column(nullable = false)
    @FullTextField(analyzer = "koreanAnalyzer")
    private String title;
    @Lob
    @Column(nullable = false)
    @FullTextField(analyzer = "koreanAnalyzer")
    private String content;
    @GenericField
    @Column(nullable = false)
    private Boolean status;
    @Column(nullable = false)
    private Integer viewCount;
    @Column(nullable = false)
    private Integer totalThumbsupVoteCount;
    @Column(nullable = false)
    private Integer totalThumbsdownVoteCount;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    @GenericField(projectable = Projectable.NO, sortable = Sortable.YES)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    @OneToMany(mappedBy = "post")
    @OrderBy("id asc")
    private Set<PostTag> postTags = new LinkedHashSet<>();
    @Version
    @Column(nullable = false)
    private Long version;

    @Builder
    public Post(Board board, Profile profile, String title, String content, boolean status, Integer viewCount, Integer totalThumbsupVoteCount,
                Integer totalThumbsdownVoteCount, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.board = board;
        this.profile = profile;
        this.title = title;
        this.content = content;
        this.status = status;
        this.viewCount = viewCount;
        this.totalThumbsupVoteCount = totalThumbsupVoteCount;
        this.totalThumbsdownVoteCount = totalThumbsdownVoteCount;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public void updateViewCount(int viewCount) {
        this.viewCount += viewCount;
    }

    public void updateBoard(Board board) {
        this.board = board;
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateStatus(boolean status) {
        this.status = status;
    }

    public void updateTotalThumbsupVoteCount(int totalThumbsupVoteCount) {
        this.totalThumbsupVoteCount += totalThumbsupVoteCount;
    }

    public void updateTotalThumbsdownVoteCount(int totalThumbsdownVoteCount) {
        this.totalThumbsdownVoteCount += totalThumbsdownVoteCount;
    }

    public void updateUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

}
