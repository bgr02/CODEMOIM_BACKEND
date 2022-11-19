package com.sideproject.codemoim.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentVote {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_vote_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id")
    private Profile profile;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id")
    private Comment comment;
    @Column(nullable = false)
    private Integer voteCount;

    @Builder
    public CommentVote(Profile profile, Comment comment, Integer voteCount) {
        this.profile = profile;
        this.comment = comment;
        this.voteCount = voteCount;
    }

    public void updateVoteCount(int voteCount) {
        this.voteCount += voteCount;
    }

}
