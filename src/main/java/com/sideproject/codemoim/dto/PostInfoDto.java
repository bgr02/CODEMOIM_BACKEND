package com.sideproject.codemoim.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostInfoDto {
    private long id;
    private String title;
    private String content;
    private String boardName;
    private String boardUrl;
    private String boardIcon;
    private String boardAuthority;
    private List<String> tagNames;
    private long profileId;
    private String profileName;
    private String profileImgUrl;
    private int contributionPoint;
    private byte userStatus;
    private int viewCount;
    private int commentCount;
    private int totalThumbsupVoteCount;
    private int totalThumbsdownVoteCount;
    private Boolean scrapFlag;
    private int scrapCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdDate;
    private Boolean selectedComment;
    private List<CommentDto> comments;
    private Boolean voteFlag;
    private int voteCount;
}
