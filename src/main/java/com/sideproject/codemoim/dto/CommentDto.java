package com.sideproject.codemoim.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentDto {
    private long id;
    private String content;
    private Boolean selectedComment;
    private int totalThumbsupVoteCount;
    private int totalThumbsdownVoteCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdDate;
    private long profileId;
    private String profileName;
    private String profileImgUrl;
    private int contributionPoint;
    private byte status;
    private Boolean voteFlag;
    private int voteCount;
}
