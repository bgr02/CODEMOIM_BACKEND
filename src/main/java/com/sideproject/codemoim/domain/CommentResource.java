//package com.sideproject.codemoim.domain;
//
//import lombok.AccessLevel;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import javax.persistence.*;
//
//@Entity
//@Getter
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//public class CommentResource {
//
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "comment_resource_id")
//    private Long id;
//    @ManyToOne(fetch = FetchType.LAZY, optional = false) // optional을 false로 지정시 해당 외래키를 NOT NULL로 설정한다는 의미
//    @JoinColumn(name = "comment_id")
//    private Comment comment;
//    @Column(nullable = false)
//    private String fileUrl;
//
//}
