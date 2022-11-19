package com.sideproject.codemoim.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class PostWithCommentDto {
    private long id;
    private String title;
    private String boardName;
    private String boardUrl;
    private String boardIcon;
    private List<String> tagNames;
    private long profileId;
    private String profileName;
    private String profileImgUrl;
    private int contributionPoint;
    private byte status;
    private int viewCount;
    private int commentCount;
    private int totalThumbsupVoteCount;
    private int totalThumbsdownVoteCount;
    private int scrapCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdDate;
    private long commentId;
}
