package com.sideproject.codemoim.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.search.engine.backend.types.Projectable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    @GenericField(projectable = Projectable.NO, sortable = Sortable.YES)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    private String tagImgUrl;
    private Integer profileFollowerCount;
    private Integer postTagCount;
    @Version
    @Column(nullable = false)
    private Long version;
    @OneToMany(mappedBy = "tag")
    @OrderBy("id asc")
    private Set<PostTag> postTags = new LinkedHashSet<>();

    @Builder
    public Tag(String name, String tagImgUrl, Integer profileFollowerCount, Integer postTagCount) {
        this.name = name;
        this.tagImgUrl = tagImgUrl;
        this.profileFollowerCount = profileFollowerCount;
        this.postTagCount = postTagCount;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateTagImgUrl(String tagImgUrl) {
        this.tagImgUrl = tagImgUrl;
    }

    public void updateProfileFollowerCount(int profileFollowerCount) {
        this.profileFollowerCount += profileFollowerCount;
    }

    public void updatePostTagCount(int postTagCount) {
        this.postTagCount += postTagCount;
    }
}
