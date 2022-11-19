package com.sideproject.codemoim.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.sql.Clob;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id")
    private Profile profile;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;
    @Lob
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private Boolean selectedComment;
    @Column(nullable = false)
    private Integer totalThumbsupVoteCount;
    @Column(nullable = false)
    private Integer totalThumbsdownVoteCount;
    @OneToMany(mappedBy = "comment", orphanRemoval = true)
    private Set<CommentVote> items = new LinkedHashSet<>();

    @Version
    @Column(nullable = false)
    private Long version;

    @Builder
    public Comment(Profile profile, Post post, String content, Boolean selectedComment, Integer totalThumbsupVoteCount, Integer totalThumbsdownVoteCount) {
        this.profile = profile;
        this.post = post;
        this.content = content;
        this.selectedComment = selectedComment;
        this.totalThumbsupVoteCount = totalThumbsupVoteCount;
        this.totalThumbsdownVoteCount = totalThumbsdownVoteCount;
    }

    public void updateSelectedComment(boolean selectedComment) {
        this.selectedComment = selectedComment;
    }

    public void updateCotent(String content) {
        this.content = content;
    }

    public void updateTotalThumbsupVoteCount(int totalThumbsupVoteCount) {
        this.totalThumbsupVoteCount += totalThumbsupVoteCount;
    }

    public void updateTotalThumbsdownVoteCount(int totalThumbsdownVoteCount) {
        this.totalThumbsdownVoteCount += totalThumbsdownVoteCount;
    }

}
