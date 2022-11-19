package com.sideproject.codemoim.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(nullable = false)
    private String username;
    private String profileImgUrl;
    @Column(nullable = false)
    private Integer contributionPoint;
    @OrderBy("id asc")
    @ManyToMany
    @JoinTable(name = "scrap", joinColumns = @JoinColumn(name = "profile_id"), inverseJoinColumns = @JoinColumn(name = "post_id"))
    private Set<Post> scraps = new LinkedHashSet<>();
    @OrderBy("id asc")
    @ManyToMany
    @JoinTable(name = "profile_tag", joinColumns = @JoinColumn(name = "profile_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new LinkedHashSet<>();

    @Version
    @Column(nullable = false)
    private Long version;

    @Builder
    public Profile(User user, String username, String profileImgUrl, Integer contributionPoint, Set<Post> scraps, Set<Tag> tags) {
        this.user = user;
        this.username = username;
        this.profileImgUrl = profileImgUrl;
        this.contributionPoint = contributionPoint;
        this.scraps = scraps;
        this.tags = tags;
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updateProfileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public void addPost(Post post) {
        this.scraps.add(post);
    }

    public void removePost(Post post) {
        this.scraps.remove(post);
    }

    public void updateContributionPoint(int contributionPoint) {
        this.contributionPoint += contributionPoint;
    }

    public void updateTags(Tag tag) {
        this.tags.add(tag);
    }

    public void removeTags(Tag tag) {
        this.tags.remove(tag);
    }

}
