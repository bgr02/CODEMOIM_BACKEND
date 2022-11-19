package com.sideproject.codemoim.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProfileFollow {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_follow_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id")
    private Profile profile;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "following_id")
    private Profile following;

    @Builder
    public ProfileFollow(Profile profile, Profile following) {
        this.profile = profile;
        this.following = following;
    }

}
