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
public class PostVote {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_vote_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id")
    private Profile profile;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;
    @Column(nullable = false)
    private Integer voteCount;

    @Builder
    public PostVote(Profile profile, Post post, Integer voteCount) {
        this.profile = profile;
        this.post = post;
        this.voteCount = voteCount;
    }

    public void updateVoteCount(int voteCount) {
        this.voteCount += voteCount;
    }
}
